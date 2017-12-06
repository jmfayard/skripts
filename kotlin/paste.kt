#!/usr/bin/env kotlin-script.sh
package paste

import copy.clipboardFile
import printList
import java.io.File


fun main(args: Array<String>) {
    pasteFiles()
}

fun pasteFiles() {
    var files = clipboardFile().readLines().map { File(it) }
    val currentDir = File(".").absoluteFile.also { println("CWD is $it") }
    val previousDir = files.firstOrNull()?.also { println("CWD was $it") } ?: run {
        println("Usage: $ copy.kt a b c d && cd /tmp && paste.kt ")
        return
    }
    files = files.takeLast(files.size - 1)

    val doesNotExist = files.filterNot { it.exists() }
    doesNotExist.map { it.absolutePath }.printList("invalid")
    val ok = (files - doesNotExist)
    for (file in ok) {
        val path = file.relativeTo(previousDir).canonicalPath
        val target = currentDir.resolve(path)
        println("${file.canonicalPath} --> ${target.canonicalPath}")
        file.copyRecursively(target, overwrite = true, onError = { f, ioException ->
            println("Error $ioException for ${f.canonicalPath}")
            OnErrorAction.SKIP
        })
    }
}

