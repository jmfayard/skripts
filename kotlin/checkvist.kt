#!/usr/bin/env kotlin-script.sh
@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package checkvist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Playing with coroutines, retrofit and the checkvist api
 * https://checkvist.com/auth/api
 ***/

val CHECKVIST_USAGE = """
checkvist.kt --help
checkvist.kt list
checkvist.kt create <<EOF
- Ingrédients
- Carottes
- Oignons

Super note ici
EOF
"""

fun main(args: Array<String>) =
        CheckvistApplication().launch(args)


class CheckvistApplication : KodeinAware {

    override val kodein = Kodein {
        import(kodeinCheckvistModule)
    }

    val coroutineApi: CheckvistCoroutineApi by instance()
    val credentials: CheckvistCredentials by instance()

    fun launch(args: Array<String>) = runBlocking {
        when (args.firstOrNull()) {
            "list" -> doStuff(coroutineApi, credentials)
            "create" -> addTask(coroutineApi, credentials, args.getOrNull(1))
            else -> println(CHECKVIST_USAGE)
        }
    }

}


class FilesReader(val stdin: Boolean = false, val files: List<String>): CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

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

suspend fun CoroutineScope.stdin(): ReceiveChannel<String> = produce {
    while (System.`in`.available() != 0) {
        channel.send(readLine() ?: return@produce)
    }
}