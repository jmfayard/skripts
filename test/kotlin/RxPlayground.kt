@file:Suppress("PackageDirectoryMismatch")

package rxplayground

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

private val DEFAULT = "switchMap"
fun main(args: Array<String>) {
    when (args.firstOrNull() ?: DEFAULT) {
        "share" -> rxShare()
        "replay" -> rxReplay()
        "subject" -> rxPublishSubject()
        "switchMap" -> rxSwitchMap()
        "buffer" -> rxBuffer()
        else -> TODO()
    }
}


fun rxShare() {
    val sharedSeconds = Observable.interval(1, SECONDS)
        .share()

    sharedSeconds.take(2).subscribe("share1".observer())
    sleep(1000)
    sharedSeconds.take(3).subscribe("share2".observer())
    sleep(4000)
    sharedSeconds.take(5).subscribe("share3".observer())
    sleep(6000)

}

fun rxReplay() {
    val sharedSeconds = Observable.interval(1, SECONDS)
        .replay(1)
        .autoConnect()

    sharedSeconds.take(2).subscribe("share1".observer())
    sleep(1000)
    sharedSeconds.take(3).subscribe("share2".observer())
    sleep(4000)
    sharedSeconds.take(5).subscribe("share3".observer())
    sleep(6000)

}

fun rxPublishSubject() {
    val src1 = Observable.interval(1, SECONDS)
        .map { "$it seconds" }
        .take(3)
    val src2 = Observable.interval(200, MILLISECONDS)
        .map { "${200 * it} miliseconds" }
        .take(10)
    val subj = PublishSubject.create<String>()
    src1.subscribe(subj)
    src2.subscribe(subj)
    Observable.merge(src1, src2).subscribe("merge".observer())
    subj.subscribe("subject".observer())
    sleep(4000)
}

fun rxSwitchMap() {
    val greeksStr = "alpha beta gamma delta epsilon zeta eta theta iota"

    val greeks = Observable.fromIterable(greeksStr.split(" "))
        .flatMap { letter ->
            Observable.timer(randomSleepTime(), MILLISECONDS).map { letter }
        }.doOnDispose { println("Disposing") }
    val switchedGreeks = intervalOf(5_000, 30_000)
        .switchMap { greeks }
    switchedGreeks.blockingSubscribe("switched".observer())

}

fun randomSleepTime() = ThreadLocalRandom.current().nextLong(2000)

fun rxBuffer() {
    val hotFire = intervalOf(100, 1000L)
        .delay(1000, MILLISECONDS)
    val coldFire = intervalOf(500)
    Observable.merge(hotFire, coldFire)
        .debounce(200, MILLISECONDS)
        .blockingSubscribe("FireBack".observer())

}

fun intervalOf(period: Int, take: Long = 3000L) =
    Observable.interval(period.toLong(), MILLISECONDS)
        .map { period * (it + 1) }
        .take(take, MILLISECONDS)

private fun sleep(ms: Int) = Thread.sleep(ms.toLong())

private fun <T> String.observer() = PrintObserver<T>(this)


class PrintObserver<T>(val name: String) : Observer<T> {
    var emitted = mutableListOf<T>()

    override fun onError(e: Throwable?) {
        println("$name: ERROR $e")
    }

    override fun onNext(t: T) {
        emitted.add(t)
        println("$name: $t")
    }

    override fun onSubscribe(d: Disposable?) {

    }

    override fun onComplete() {}

}

