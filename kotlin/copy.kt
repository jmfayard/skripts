#!/usr/bin/env kotlin-script.sh
package copy

import printList
import java.io.File


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: $ copy.kt a b c d && cd /tmp && paste.kt ")
        return
    }
    copyFiles(*args)
}

fun copyFiles(vararg paths: String) {
    val files = paths.map { File(it) }
    files.filterNot { it.exists() }.map { it.absolutePath }.printList("invalid")
    val exists = files.filter { it.exists() }.map { it.absolutePath }.printList("copy")
    clipboardFile().writer().use { writer ->
        writer.appendln(File(".").absolutePath)
        for (line in exists) {
            writer.appendln(line)
        }
    }
}

fun clipboardFile(create: Boolean = false): File {
    val user = System.getenv("USER")
    return File("/users/$user").resolve(".clipboard").also { file ->
        println(file.absolutePath)
        if (!file.exists() || create) {
            file.createNewFile()
        }
    }
}