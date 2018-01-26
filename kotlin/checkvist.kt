#!/usr/bin/env kotlin-script.sh
@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package checkvist

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking
import java.io.File

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

fun main(args: Array<String>) = runBlocking {
    when (args.firstOrNull()) {
        "list" -> doStuff(app().coroutineApi, app().credentials)
        "create" -> addTask(app().coroutineApi, app().credentials, args.getOrNull(1))
        else -> println(CHECKVIST_USAGE)
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