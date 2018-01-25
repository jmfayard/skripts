@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package checkvist

import com.squareup.moshi.Moshi
import debug
import debugList
import environmentVariable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult

suspend fun addTask(api: CheckvistCoroutineApi, credentials: CheckvistCredentials, param: String?) {
    val stdin = if (param == null) checkvist.stdin() else textFrom(param)
    val title = stdin.receiveOrNull() ?: run { println(CHECKVIST_USAGE); return }
    val parentTask = api.createTask(CNewTask(content = title), credentials.defaultList).checkOk()
    var position = 0
    var noteFound = false
    var noteContent = ""
    for (i in stdin) {
        val line = i.trim().trimMargin("- ")
        noteFound = noteFound or line.isBlank()

        if (noteFound) {
            noteContent += line
        } else {
            position++
            val childrenTask = CNewTask(content = line, parent_id = parentTask.id, position = -1)
            api.createTask(childrenTask, credentials.defaultList).checkOk()
        }
    }
    if (noteContent.trim().isNotBlank()) {
        println("ok")
        api.createNote(noteContent, parentTask)
        println("bye")
    }
}

suspend fun textFrom(param: String): ReceiveChannel<String> = produce {
    val input = """
Ingrédients
Carottes
Oignons

Super note ici""".trim()

    for (line in input.splitToSequence('\n')) {
        send(line)
    }
}

@Suppress("UNUSED_VARIABLE")
suspend fun doStuff(api: CheckvistCoroutineApi, credentials: CheckvistCredentials) {
    println(
        """"
        |USER=${credentials.USER}
        |CHECKVIST_KEY=${credentials.CHECKVIST_KEY}
        |AUTH=${credentials.auth()}
    """.trimMargin()
    )
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

    @POST("checklists/{list}/tasks/{task}/comments.json")
    @FormUrlEncoded
    fun createNote(
            @Field("comment[comment]") note: String,
            @Path("task") task: Int,
            @Path("list") list: Int,
            @Header("Authorization") auth: String
    ): Call<CNote>

}

class CheckvistCoroutineApi(val api: CheckvistApi, val credentials: CheckvistCredentials) {
    suspend fun login(): Result<String> = api.login(credentials.USER, credentials.CHECKVIST_KEY).awaitResult()
    suspend fun currentUser(): Result<CUser> = api.currentUser(credentials.auth()).awaitResult()
    suspend fun lists(): Result<List<CList>> = api.checklists(credentials.auth()).awaitResult()
    suspend fun list(list: Int): Result<CList> = api.checklist(list, credentials.auth()).awaitResult()
    suspend fun tasks(list: Int): Result<List<CTask>> = api.checkTasks(list, credentials.auth()).awaitResult()
    suspend fun createTask(task: CNewTask, list: Int): Result<CTask> =
        api.createTask(task, list, credentials.auth()).awaitResult()

    suspend fun deleteTask(task: Int, list: Int): Result<CTask> =
        api.deleteTask(task, list, credentials.auth()).awaitResult()

    suspend fun getNotes(task: CTask) = api.getNotes(task.id, task.checklist_id, credentials.auth()).awaitResult()
    suspend fun createNote(comment: String, task: CTask) =
        api.createNote(comment, task.id, task.checklist_id, credentials.auth()).awaitResult()
}


suspend fun linesFrom(stdin: Boolean = false, files: Array<String>) = FilesReader(stdin, files.toList()).lines()