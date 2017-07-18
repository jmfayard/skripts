import io.reactivex.Observable
import org.junit.Test
import rx.lang.kotlin.toObservable
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


}