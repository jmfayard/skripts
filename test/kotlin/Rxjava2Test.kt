import io.reactivex.Observable
import org.junit.Test

import java.lang.RuntimeException

/**
 * Created by jmfayard on 18.07.17.
 */

class Rxjava2Test {
    @Test fun values() {
        val t = Observable.just(1, 2, 3)
                .test()
                .assertValues(1, 2, 3)
                .await()
                .assertComplete()
                .assertNever(4)

        t.values().debug("values")

    }

    @Test fun error() {
        Observable.error<Int>(RuntimeException("Failed"))
                .test()
                .assertErrorMessage("Failed")
    }

    @Test fun cache() {
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



}