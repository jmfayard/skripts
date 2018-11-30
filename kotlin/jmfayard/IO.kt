@file:Suppress("NOTHING_TO_INLINE")

package jmfayard

import org.intellij.lang.annotations.Language
import org.zeroturnaround.exec.ProcessExecutor
import java.io.File

fun executeBashCommand(vararg args: String): String {
    println("# " + args.joinToString(separator = " "))
    val program = args.first()
    return ProcessExecutor().command(*args)
        .redirectErrorStream(true)
        .readOutput(true).execute()
        .outputUTF8()
}

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

fun readableFile(path: String, directory: Boolean = false): File = File(path).also {
    require(it.canRead()) { "Cannot read file ${it.absolutePath}" }
    require(it.isDirectory xor directory.not()) { "Not a directory: ${it.absolutePath}" }
}

fun writableFile(path: String): File = File(path).also {
    it.createNewFile()
}

inline fun <T> List<T>.printList(name: String): List<T> {
    println("<List name=$name size=$size>")
    forEachIndexed { i, t ->
        println("$name[$i] : $t")
    }
    println("</List name=$name size=$size>")
    return this
}

inline fun <K, V> Map<K, V>.printMap(name: String): Map<K, V> {
    println("<Map name=$name size=$size>")
    for ((k, v) in this) {
        println("$name[$k] : $v")
    }
    println("</Map name=$name size=$size>")
    return this
}
