#!/usr/bin/env kotlin-script.sh
package androidstrings

import krangl.DataFrame
import krangl.dataFrameOf
import krangl.print
import krangl.writeCSV
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import printList
import printMap
import readableFile
import java.io.File

fun main(args: Array<String>) {
    println("Hello World!")
    val files = findFiles("/Users/jmfayard/Dev/mautinoa/mautinoa-app").printList("files")

    val map = parseAndroidStringFile("/Users/jmfayard/Dev/mautinoa/mautinoa-app/app/src/main/res/values/strings.xml").printMap("strings")

    krangleStrings(map, "/Users/jmfayard/Dev/mautinoa/mautinoa-app")
}


fun findFiles(path: String): List<File> {
    val base = readableFile(path, directory = true)
    val files = base.walkTopDown().filter { it.name == "strings.xml" }
    return files.toList()
}

fun parseAndroidStringFile(path: String): Map<String, String> {
    val document = SAXBuilder().build(readableFile(path))
    val children = document.rootElement.children
    return children.associate { e: Element -> e.asAndroidString() }
}

private fun Element.asAndroidString(): Pair<String, String> =
        this.getAttribute("name").value to this.text

fun krangleStrings(map: Map<String, String>, path: String) {
    val dest = readableFile(path, directory = true).resolve("strings.csv")

    val values = map.entries.sortedBy { it.key }.flatMap { listOf(it.key, it.value, "") }.toTypedArray()
    val df: DataFrame = dataFrameOf("name", "EN", "PT")(*values)
    df.print()
    df.writeCSV(dest)
    println("Written to ${dest.absolutePath}")
}