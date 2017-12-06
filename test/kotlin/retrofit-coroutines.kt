import com.squareup.moshi.Moshi
import io.kotlintest.matchers.be
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import ru.gildor.coroutines.retrofit.awaitResult


/**
 ** Trying out: Kotlin Coroutines for Retrofit
 ** https://github.com/gildor/kotlin-coroutines-retrofit
 */

class KotlinCoroutinesRetrofitTest : StringSpec() { init {
    val args = mapOf("gender" to "MALE", "age" to "42")

    "Call.await()" {
        runBlocking {
            val response = IO.httpbinService.get(args).await().debug("response")
            response.args shouldBe args
        }
    }

    "Call.await() throw an exception" {
        shouldThrow<HttpException> {
            runBlocking {
                IO.httpbinService.status(401).await()
            }
        }
    }

    "Call.awaitResponse()" {
        runBlocking {
            val response = IO.httpbinService.status(304).awaitResponse().debug("response")
            response.message() shouldBe "NOT MODIFIED"
        }
    }

    "Call.awaitResult()" {
        runBlocking {
            IO.httpbinService.get(args).awaitResult() should be an Result.Ok::class
            IO.httpbinService.status(404).awaitResult() should be an Result.Error::class
            IO.httpbinService.invalid().awaitResult() should be an Result.Exception::class
        }
    }
}
}

object IO {
    val moshi = Moshi.Builder().build()
    val response = moshi.adapter(HttpbinResponse::class.java)
    val retrofit = buildRetrofit {
        baseUrl("http://httpbin.org/")
        addConverterFactory(MoshiConverterFactory.create())
        buildOk {
            val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
            addNetworkInterceptor(logger)
        }
    }
    val httpbinService = retrofit.create(ApiService::class.java)

}

interface ApiService {

    @GET("/get")
    fun get(@QueryMap map: Map<String, String>): Call<HttpbinResponse>

    /** Returns given HTTP Status code. **/
    @GET("/status/{code}")
    fun status(@Path("code") code: Int): Call<HttpbinResponse>


    @GET("/get")
    fun invalid(): Call<Boolean>
}

typealias ValuesMap = Map<String, Any>?
data class HttpbinResponse(
        val args: ValuesMap = null,
        val headers: ValuesMap = null,
        val origin: String? = null,
        val url: String? = null,
        val `user-agent`: String? = null,
        val data: String? = null,
        val files: ValuesMap = null,
        val form: ValuesMap = null,
        val json: ValuesMap = null,
        val gzipped: Boolean? = null,
        val deflated: Boolean? = null,
        val method: String? = null,
        val cookies: ValuesMap = null
)