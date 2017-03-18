import IO.response
import IO.retrofit
import IO.service
import com.squareup.moshi.Moshi
import io.kotlintest.matchers.be
import io.kotlintest.specs.FeatureSpec
import io.kotlintest.specs.StringSpec
import jdk.nashorn.internal.ir.ObjectNode
import json2data.moshiAdapter
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import ru.gildor.coroutines.retrofit.awaitResult
import rx.Single


class RetrofitTests : StringSpec() { init {
    val args = mapOf("gender" to "MALE", "age" to "42")
    val json = """{"args":{"gender":"MALE","age":"42"},"method":"GET","origin":"localhost"}""".trim()

    "Testing Moshi" {
        val response = HttpbinResponse(args = args, origin = "localhost", method = "GET")
        IO.response.toJson(response).shouldBe(json)
        IO.response.fromJson(json).shouldBe(response)
    }

    "Call.await()" {
        runBlocking {
            val response = IO.service.get(args).await().debug("response")
            response.args shouldBe args
        }
    }

    "Call.await() throw an exception" {
        shouldThrow<HttpException> {
            runBlocking {
                IO.service.status(401).await()
            }
        }
    }

    "Call.awaitResponse()" {
        runBlocking {
            val response = IO.service.status(304).awaitResponse().debug("response")
            response.message() shouldBe "NOT MODIFIED"
        }
    }

    "Call.awaitResult()" {
        runBlocking {
            IO.service.get(args).awaitResult() should be an Result.Ok::class
            IO.service.status(404).awaitResult() should be an Result.Error::class
            IO.service.invalid().awaitResult() should be an Result.Exception::class
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
    val service = retrofit.create(ApiService::class.java)

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