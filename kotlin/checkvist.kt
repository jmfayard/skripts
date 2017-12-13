#!/usr/bin/env kotlin-script.sh
@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package checkvist

import checkOk
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import debug
import debugList
import environmentVariable
import joinLines
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.runBlocking
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.awaitResult
import java.io.File

/**
 * Playing with coroutines, retrofit and the checkvist api
 * https://checkvist.com/auth/api
 ***/

val USAGE = """
checkvist.kt --help
checkvist.kt list
checkvist.kt create <<EOF
- Ingrédients
- Carottes
- Oignons

Super note ici
EOF
"""

fun main(args: Array<String>) = runBlocking {
    when (args.firstOrNull()) {
        "list" -> doStuff(app().coroutineApi, app().credentials)
        "create" -> addTask(app().coroutineApi, app().credentials)
        else -> println(USAGE)
    }
}

private fun app() = CheckvistComponent


class FilesReader(val stdin: Boolean = false, val files: List<String>) {
    val existingFiles = files.map { File(it) }.filter { it.canRead() }
    val invalidFiles = files.filterNot { File(it).canRead() }

    suspend fun lines(): ReceiveChannel<String> = produce {
        if (stdin && System.`in`.available() > 0) {
            for (line in stdin()) channel.send(line)
        }
        for (file in existingFiles) {
            file.bufferedReader().useLines { seq ->
                for (line in seq) {
                    channel.send(line)
                }
            }
        }
    }

}

suspend fun stdin(): ReceiveChannel<String> = produce {
    while (System.`in`.available() != 0) {
        channel.send(readLine() ?: return@produce)
    }
}