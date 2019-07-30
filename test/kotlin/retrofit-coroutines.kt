import com.squareup.moshi.Moshi
import io.kotlintest.matchers.beOfType
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Retrofit
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

    "ApiTEST" {
        runBlocking {
            IO.httpbinService.raw().await().debug("ApiTEST") shouldBe ApiTest(ok2 = true)
        }
    }

    "ApiTEST2" {
        runBlocking {
            IO.httpbinService.raw().await().debug("ApiTEST")
        }
    }

    "Call.await() ta mere" {
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
            IO.httpbinService.get(args).awaitResult() should beOfType<Result.Ok<*>>()
            IO.httpbinService.status(404).awaitResult() should beOfType<Result.Error>()
            IO.httpbinService.invalid().awaitResult() should beOfType<Result.Exception>()
        }
    }
}
}

object IO {
    val LEVEL = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC

    val printlnLogger = object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            println(message)
        }
    }
    val logger: HttpLoggingInterceptor = HttpLoggingInterceptor(printlnLogger).also { it.level = LEVEL }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder().addNetworkInterceptor(logger).build()

    val moshi = Moshi.Builder().build()

    val response = moshi.adapter(HttpbinResponse::class.java)

    val retrofit = Retrofit.Builder()
        .baseUrl("http://httpbin.org/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val httpbinService = retrofit.create(ApiService::class.java)
}

data class ApiTest(val ok2: Boolean)

interface ApiService {

    @GET("https://gist.githubusercontent.com/jmfayard/d3965f2b34d9f9cc6ba88c9b624f4ed4/raw/017d28af92a498a090ec1db92177064050b42135/example.json")
    fun raw(): Call<ApiTest>

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
