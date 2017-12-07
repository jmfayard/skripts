import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import org.intellij.lang.annotations.Language
import org.zeroturnaround.exec.ProcessExecutor
import retrofit2.Retrofit
import ru.gildor.coroutines.retrofit.Result
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun <T : Any> Result<T>.checkOk(): T {
    if(this is Result.Ok) {
        return value
    } else {
        error("Http call failed: ${this}")
    }
}


fun <T> environmentVariable(notfoundMesssage: String) : ReadOnlyProperty<T, String> = LazyEnvironmentVariable(notfoundMesssage, null)

fun <T> optionalEnvironmentVariable(default: String) : ReadOnlyProperty<T, String> = LazyEnvironmentVariable("", default)


class LazyEnvironmentVariable<in T>(val notfoundMesssage: String, val default: String?) : ReadOnlyProperty<T, String> {
    override fun getValue(thisRef: T, property: KProperty<*>): String {
        val name: String = property.name
        val value = System.getenv(name) ?: default
        if (value != null) return  value
        else {
            val missingKey = "ERROR: Missing environment variable\n $ export $name='xxxxx'\n\n$notfoundMesssage"
            System.err.println(missingKey)
            System.exit(1) ; error("missingKey")
        }
    }
}

fun <T> T.debug(name: String): T {
    println("DEBUG: ${name} = ${toString()}")
    return this
}

fun <T> List<T>.debugList(name: String): List<T> {
    forEachIndexed { i, t ->
        println("$name[$i] : $t")
    }
    return this
}

fun BufferedSink.newLine() = writeUtf8("\n")


fun File.okSource(): BufferedSource = Okio.buffer(Okio.source(this))

fun File.okSink(): BufferedSink = Okio.buffer(Okio.sink(this))

fun File.okAppendingSink(): BufferedSink = Okio.buffer(Okio.appendingSink(this))


fun osxOpenFile(file: File) {
    println("$ /usr/bin/open ${file.absolutePath}")
    require(file.canRead()) { System.exit(1); "ERROR File not found" }
    val errorValue = ProcessExecutor().command("/usr/bin/open", file.absolutePath).execute().exitValue
    check(errorValue == 0) { "Process exited with error: $errorValue" }
}

fun printAsTable(vararg pairs: Pair<Any, Any>) {
    if (pairs.isEmpty()) return
    val length = 3 + pairs.map { key -> key.first.toString().count() }.max()!!
    val format = "%-${length}s %s"
    for ((first, second) in pairs) {
        println(String.format(format, first, second))
    }
}

fun resourceFile(@Language("File") path: String, write: Boolean = false): File {
    return File("test/resources/$path").apply {
        val condition = if (write) canWrite() else canRead()
        check(condition) { "Cannot open resourceFile at $absolutePath" }
    }
}

fun buildRetrofit(init: Retrofit.Builder.() -> Unit): Retrofit {
    val builder = Retrofit.Builder()
    builder.init()
    return builder.build()
}

fun buildOk(init: OkHttpClient.Builder.() -> Unit): OkHttpClient {
    val builder = OkHttpClient.Builder()
    builder.init()
    return builder.build()
}

fun buildRequest(init: Request.Builder.() -> Unit): Request {
    val builder = Request.Builder()
    builder.init()
    return builder.build()
}

fun Request.Builder.buildUrl(init: HttpUrl.Builder.() -> Unit) {
    val builder = HttpUrl.Builder()
    builder.init()
    url(builder.build())
}

fun fail(message: String): Nothing = throw AssertionError(message)