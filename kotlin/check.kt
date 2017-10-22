#!/usr/bin/env kotlin-script.sh
package check

import baiscAuthenticator
import com.squareup.moshi.Moshi
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import debug
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.gildor.coroutines.retrofit.await
import ru.gildor.coroutines.retrofit.awaitResponse
import java.io.File

fun main(args: Array<String>) = runBlocking {

    with(CheckvistComponent()) {
        val response = api.login(config[checkvist.username], config[checkvist.apikey]).debug("call").await()
        println(response)

        val lists = api.checklists().await()
        println(lists)

    }
}


object checkvist : ConfigSpec("checkvist") {
    val username = required<String>("username")
    val apikey = required<String>("apikey")
}


class CheckvistComponent {

    var config: Config = Config.invoke { addSpec(checkvist) }
            .withSourceFrom.env()
    var moshi: Moshi = Moshi.Builder().build()
    val authenticator = baiscAuthenticator(config[checkvist.username], config[checkvist.apikey]) { url ->
        url.host() == "checkvist.com" && !url.toString().contains("login")
    }
    val client: OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(Level.BODY))
            .addNetworkInterceptor(authenticator)
            .build()
    val api: CheckvistApi = Retrofit.Builder()
            .baseUrl("https://checkvist.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create(CheckvistApi::class.java)
}

typealias UnknownHttpResponse = java.lang.Object
interface CheckvistApi {

    @GET("auth/login.json")
    fun login(@Query("username") username: String, @Query("remote_key") key: String): Call<UnknownHttpResponse>

    @GET("checklists.json")
    fun checklists(): Call<List<CList>>
}

data class CList(
        val id: Int = 0,
        val name: String = "",
        val role: Int = 0,
        val updated_at: String = "",
        val task_count: Int = 0,
        val task_completed: Int = 0,
        val read_only: Boolean = false
)
/**
<id>1</id>
<name>EAP checklist</name>
<public>false</public>
<role>1</role>
<updated_at>2009-05-16T11:20:31Z</updated_at>
<task_count>3</task_count>
<task_completed>1</task_completed>
<read_only>true</read_only>
 */