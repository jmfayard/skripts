package pingpong

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ThreadLocalRandom

fun main(args: Array<String>) = runBlocking<Unit> {

    val channel = Channel<Int>()

    val job1 = launch {
        player("Rafael Nadal", channel)
    }
    val job2 = launch {
        player("Gael Monfils", channel)
    }
    channel.send(1)
    listOf(job1, job2).map { it.join() }

}

suspend fun player(name: String, court: Channel<Int>) {
    for (ball in court) {
        if (ThreadLocalRandom.current().nextInt(13) == 0) {
            println("Player $name Missed")
            court.close()
        } else {
            println("${name.padEnd(10)} hits $ball")
            court.send(ball + 1)
        }
    }
}