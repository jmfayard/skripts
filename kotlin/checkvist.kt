#!/usr/bin/env kotlin-script.sh
@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package checkvist

import checkOk
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import debug
import environmentVariable
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import debugList
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult

/**
 * Playing with coroutines, retrofit and the checkvist api
 * https://checkvist.com/auth/api
 ***/

fun main(args: Array<String>) = runBlocking {
    doStuff(app().coroutineApi, app().credentials)

}

suspend fun doStuff(api: CheckvistCoroutineApi, credentials: CheckvistCredentials) {
    println(""""
        |USER=${credentials.USER}
        |CHECKVIST_KEY=${credentials.CHECKVIST_KEY}
        |AUTH=${credentials.auth()}
    """.trimMargin())
    val login: String = api.login().checkOk().debug("login")
    val currentUser: CUser = api.currentUser().checkOk().debug("currentUser")
    val lists: List<CList> = api.lists().checkOk().debugList("lists")
    val list: CList = lists.first { list -> list.name == "api" }.debug("list")
    val tasks: List<CTask> = api.tasks(list.id).checkOk().debugList("tasks")
    val firstTask: CTask = tasks.first()
    check(firstTask.content == "task")
    val notes: List<CNote> = api.getNotes(firstTask).checkOk().debugList("notes")
    check(notes.first().comment == "note")

    val newTask = api.createTask(CNewTask(content = "Try kotlin #coroutines ^asap"), list.id).checkOk().debug("newTask")
    api.deleteTask(newTask.id, list.id).debug("deleted")

}


private fun app() = CheckvistComponent

object CheckvistComponent {

    val LEVEL = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC

    val logger: HttpLoggingInterceptor = HttpLoggingInterceptor(::println).setLevel(LEVEL)

    val okHttpClient: OkHttpClient = OkHttpClient.Builder().addNetworkInterceptor(logger).build()

    val moshi: Moshi = Moshi.Builder().build()

    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://checkvist.com/")
            .client(okHttpClient)
            .validateEagerly(true)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val api: CheckvistApi by lazy { retrofit.create(CheckvistApi::class.java) }

    val coroutineApi: CheckvistCoroutineApi by lazy { CheckvistCoroutineApi(api, credentials) }

    val credentials: CheckvistCredentials = object : CheckvistCredentials {
        override val CHECKVIST_KEY by environmentVariable("No OpenApi Key found. Grab one at https://checkvist.com/auth/profile")
        override val USER = "jmfayard@gmail.com"
    }
}

typealias Unknown = java.lang.Object

interface CheckvistApi {

    @POST("auth/login.json")
    fun login(@Query("username") username: String, @Query("remote_key") remote_key: String): Call<String>

    @GET("auth/curr_user.json")
    fun currentUser(@Header("Authorization") auth: String): Call<CUser>

    @GET("checklists.json")
    fun checklists(@Header("Authorization") auth: String): Call<List<CList>>

    @GET("checklists/{list}.json")
    fun checklist(@Path("list") list: Int, @Header("Authorization") auth: String): Call<CList>

    @GET("checklists/{list}/tasks.json")
    fun checkTasks(@Path("list") list: Int, @Header("Authorization") auth: String): Call<List<CTask>>

    @POST("checklists/{list}/tasks.json")
    fun createTask(@Body task: CNewTask, @Path("list") list: Int, @Header("Authorization") auth: String): Call<CTask>

    @DELETE("checklists/{list}/tasks/{task}.json")
    fun deleteTask(@Path("task") task: Int, @Path("list") list: Int, @Header("Authorization") auth: String): Call<CTask>

    @GET("checklists/{list}/tasks/{task}/comments.json")
    fun getNotes(@Path("task") task: Int, @Path("list") list: Int, @Header("Authorization") auth: String): Call<List<CNote>>

}

class CheckvistCoroutineApi(val api: CheckvistApi, val credentials: CheckvistCredentials) {
    suspend fun login(): Result<String> = api.login(credentials.USER, credentials.CHECKVIST_KEY).awaitResult()
    suspend fun currentUser(): Result<CUser> = api.currentUser(credentials.auth()).awaitResult()
    suspend fun lists(): Result<List<CList>> = api.checklists(credentials.auth()).awaitResult()
    suspend fun list(list: Int): Result<CList> = api.checklist(list, credentials.auth()).awaitResult()
    suspend fun tasks(list: Int): Result<List<CTask>> = api.checkTasks(list, credentials.auth()).awaitResult()
    suspend fun createTask(task: CNewTask, list: Int): Result<CTask> = api.createTask(task, list, credentials.auth()).awaitResult()
    suspend fun deleteTask(task: Int, list: Int): Result<CTask> = api.deleteTask(task, list, credentials.auth()).awaitResult()
    suspend fun getNotes(task: CTask) = api.getNotes(task.id, task.checklist_id, credentials.auth()).awaitResult()
}

interface CheckvistCredentials {
    val CHECKVIST_KEY: String
    val USER: String
    fun auth() = Credentials.basic(USER, CHECKVIST_KEY)
}


data class CUser(
        val email: String = "",
        val id: Int = 0,
        val username: String = "",
        val pro: Boolean = false,
        val email_md5: String = ""
)

data class CList(
        val id: Int = 0,
        val name: String = "",
        val options: Int = 0,
        val public: Boolean = false,
        val updated_at: String = "",
        @Json(name = "markdown?")
        val markdown: Boolean = false,
        val archived: Boolean = false,
        val read_only: Boolean = false,
        val user_count: Int = 0,
        val user_updated_at: String = "",
        val percent_completed: Double = 0.0,
        val task_count: Int = 0,
        val task_completed: Int = 0,
        val tags: Unknown,
        val tags_as_text: String = ""
)

data class CTask(
        val id: Int = 0,
        val parent_id: Int = 0,
        val checklist_id: Int = 0,
        val status: Int = 0,
        val position: Int = 0,
        val tasks: List<Int> = emptyList(),
        val update_line: String = "",
        val updated_at: String = "",
        val due: String = "",
        val content: String = "",
        val collapsed: Boolean = false,
        val comments_count: Int = 0,
        val assignee_ids: List<Int> = emptyList(),
        val due_user_ids: List<Int> = emptyList(),
        val details: Unknown,
        val tags: Unknown,
        val tags_as_text: String = "",
        val color: Unknown
)
data class CNewTask (
        val parent_id: Int = 0,
        val position: Int = 0,
        val content: String = ""
)
data class CNote (
        val comment: String = "",
        val created_at: String = "",
        val id: Int = 0,
        val task_id: Int = 0,
        val updated_at: String = "",
        val user_id: Int = 0,
        val username: String = ""
)

