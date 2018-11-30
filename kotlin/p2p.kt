package p2p

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield

fun main(args: Array<String>) {
    simulation()
}

fun simulation() = runBlocking {
    val networkChannel = Channel<Int>(Channel.UNLIMITED)
    val nexus5x = Nearby("nexus5x", networkChannel)
    val nexus6p = Nearby("nexus6p", networkChannel)

    val job1 = launch(coroutineContext) {
        val job1 = launch(coroutineContext) { nexus5x.start(1500, 12000) }
        val job2 = launch(coroutineContext) { nexus6p.start(1200, 6000) }
        job1.join()
        job2.join()
    }
    val job2 = launch(coroutineContext) {
        val app = App("nexus5x")
        run { app.sendToNearby(nexus5x) }
        run { app.receiveFromNearby(nexus5x) }
    }
    val job3 = launch(coroutineContext) {
        val app = App("nexus6p")
        run { app.sendToNearby(nexus6p) }
        run { app.receiveFromNearby(nexus6p) }
    }

    listOf(job1, job2, job3).forEach { it.join() }
}

class App(val name: String) {
    suspend fun sendToNearby(nearby: Nearby) {
        for (nb in 1..10) {
            delay(50)
            println("App $name : I sent $nb")
            nearby.sendMessageAsync(nb)
            delay(100)
        }
    }

    suspend fun receiveFromNearby(nearby: Nearby) {
        for (i in 1..10) {
            delay(100)
            val nb = nearby.waitForMessage()
            println("App $name : I Received $nb")
            delay(150)
        }
    }
}

class Nearby(val name: String, val peer: Channel<Int>) {
    override fun toString(): String = "Nearby($name)"
    suspend fun sendMessageAsync(data: Int) {
        appToNearby.send(data)
    }

    suspend fun waitForMessage(): Int {
        return nearbyToApp.receive()
    }

    private val appToNearby: Channel<Int> = Channel(Channel.UNLIMITED)
    private val nearbyToApp: Channel<Int> = Channel(Channel.UNLIMITED)

    suspend fun start(sleep: Long, work: Long) {
        val nearby = this
        println("Starting Nearby, will sleep $sleep miliseconds")
        delay(sleep)
        println("Will be receiving during $work seconds")
        var sendNext: Int? = null
        withTimeout(work) {
            whileSelect {
                peer.onReceiveOrNull { data ->
                    if (data == null) {
                        println("$nearby: peer aborted, doing that too")
                        false
                    } else {
                        println("$nearby: Received $data from peer")
                        sendNext = data
                        true
                    }
                }
                appToNearby.onReceive { data ->
                    println("$nearby: Received $data from app, forwarding it to peer")
                    peer.send(data)
                    true
                }
                if (sendNext != null) {
                    nearbyToApp.onSend(sendNext!!) {
                        println("$nearby: Sent $sendNext to app")
                        sendNext = null
                        true
                    }
                }
                onTimeout(50) {
                    yield()
                    true
                }
            }
        }
        println("$nearby: timeout reached, I will abort here")
        peer.close()
    }
}
