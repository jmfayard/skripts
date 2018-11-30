import io.reactivex.Observable
import io.reactivex.RxMarble
import io.reactivex.Single
import io.reactivex.exceptions.CompositeException
import io.reactivex.rxkotlin.merge
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by jmfayard on 18.07.17.
 */

class Rxjava2Test {

    @Test
    fun emitListWithTimer() {
        // better alternatives to Observable.interval() to emit a list of values
        val valuesToEmit = listOf("un", "deux", "trois", "quatre", "cinq")
        val observable = valuesToEmit.mapIndexed { index, value ->
            Observable.timer(index * 50L, TimeUnit.MILLISECONDS, Schedulers.computation()).map { value }
        }.merge()
        observable.blockingSubscribe(::println)
    }

    @Test
    fun join() {

        fun <T> just(observable: Observable<T>) = io.reactivex.functions.Function { i: Int ->
            observable
        }

        val scheduler = TestScheduler()
        val marble = RxMarble(scheduler = scheduler)
        val ints = marble.cold(
            "--1---3-4---5--6----7-9", List(10) { it * it })
        val lifecycle = marble.cold(
            "----1-----2---1---2--1-", listOf(0, 1)
        )
    }

    @Test
    fun values() {
        val t = Observable.just(1, 2, 3)
            .test()
            .assertValues(1, 2, 3)
            .await()
            .assertComplete()
            .assertNever(4)

        t.values().debug("values")
    }

    @Test
    fun error() {
        Observable.error<Int>(RuntimeException("Failed"))
            .test()
            .assertErrorMessage("Failed")
    }

    @Test
    fun cache() {
        var counter = 0
        val observable = Observable.create<Int> { emitter ->
            counter++
            println("Creating observable")
            println("Emitting 5")
            emitter.onNext(5)
            println("Completing")
            emitter.onComplete()
        }.cache()

        observable.test().assertValue(5)
        observable.test().assertValue(5)
        assert(counter == 1)
    }

    @Test
    fun errors() {
        val niceError = RuntimeException("Nice Error")
        val flow: Single<Int> = Single.error(CompositeException(RuntimeException("foo"), RuntimeException("bar")))

        flow.onErrorReturnItem(1)
            .test().await().assertValue(1).assertComplete()

        flow.onErrorReturn({ _ -> 1 })
            .test().await().assertValue(1).assertComplete()

        flow.onErrorResumeNext { _ -> Single.error(niceError) }
            .test().await().assertError(niceError)
    }
}
