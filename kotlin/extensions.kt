import okhttp3.*
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import org.zeroturnaround.exec.ProcessExecutor
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

fun <T> T.debug(name: String): T {
    println("DEBUG: ${name} = ${toString()}")
    return this
}
fun <T> List<T>.printList(name: String){
    forEachIndexed { i, t ->
        println("$name[$i] : $t")
    }
}

fun BufferedSink.newLine() = writeUtf8("\n")


fun File.okSource(): BufferedSource = Okio.buffer(Okio.source(this))

fun File.okSink(): BufferedSink = Okio.buffer(Okio.sink(this))

fun File.okAppendingSink(): BufferedSink = Okio.buffer(Okio.appendingSink(this))


fun osxOpenFile(file: File)  {
    println("$ /usr/bin/open ${file.absolutePath}")
    require(file.canRead()) { System.exit(1); "ERROR File not found" }
    val errorValue = ProcessExecutor().command("/usr/bin/open", file.absolutePath).execute().exitValue
    if (errorValue!=0) { "Process exited with error: $errorValue" }
}

public fun printAsTable(vararg pairs: Pair<Any, Any>){
    if (pairs.isEmpty()) return
    val length = 3 + pairs.map { key -> key.first.toString().count() }.max()!!
    val format = "%-${length}s %s"
    for ((first, second) in pairs) {
        println(String.format(format, first, second))
    }
}


fun baiscAuthenticator(username: String, password: String, predicate: (HttpUrl) -> Boolean) = Interceptor { chain ->
    val url = chain.request().url()
    val request = if (predicate(url)) {
        val credential = Credentials.basic(username, password)
        chain.request().newBuilder()
                .header("Authorization", credential)
                .build()
    } else {
        chain.request()
    }
    chain.proceed(request)
}


fun buildRetrofit(init: Retrofit.Builder.() -> Unit) : Retrofit {
    val builder = Retrofit.Builder()
    builder.init()
    return builder.build()
}

fun buildOk(init: OkHttpClient.Builder.() -> Unit) : OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.init()
    return builder.build()
}

fun buildRequest(init: Request.Builder.() -> Unit): Request {
    val builder = Request.Builder()
    builder.init()
    return builder.build()
}

fun Request.Builder.buildUrl(init: HttpUrl.Builder.() -> Unit): Unit {
    val builder = HttpUrl.Builder()
    builder.init()
    url(builder.build())
}

fun fail(message: String) : Nothing = throw AssertionError(message)