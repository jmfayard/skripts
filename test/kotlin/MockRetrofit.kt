import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.time.Duration
import java.util.concurrent.TimeUnit

class MockRetrofitTest : StringSpec() { init {

    val WITH_ERRORS = false

    val retrofit = Retrofit.Builder()
        .baseUrl("http://httpbin.org")
        .build()

    val mockRetrofit = MockRetrofit.Builder(retrofit)
        .networkBehavior(
            NetworkBehavior.create().apply {
                setDelay(300, TimeUnit.MILLISECONDS)
                setErrorPercent(if (WITH_ERRORS) 15 else 0)
                setVariancePercent(60)
                setFailurePercent(if (WITH_ERRORS) 20 else 0)
            }
        ).build()

    val httpbin = mockRetrofit
        .create(PingService::class.java)

    "Ping".config(timeout = Duration.ofSeconds(if (WITH_ERRORS) 400 else 900)) {
        val service = httpbin.returningResponse(true)
        service.ping().execute().isSuccessful shouldBe true
    }
}
}

interface PingService {

    /**   Returns Origin IP   */
    @GET("/ping")
    fun ping(): Call<Response<Boolean>>
}
