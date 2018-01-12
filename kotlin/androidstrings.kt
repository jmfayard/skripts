#!/usr/bin/env kotlin-script.sh
package androidstrings

import debug
import krangl.*
import org.apache.commons.csv.CSVFormat
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import printList
import printMap
import readableFile
import java.io.File

private val DEFAULT = "csv2xml"
private val APP = "/Users/jmfayard/Dev/mautinoa/mautinoa-app"
private fun usage() : Nothing {
    println("""
$ androidstrings.kt COMMAND OPTIONS

Convert localisations between the android xml format and CSV (thus excel)

COMMAND can be
    help            -> print usage
    files           -> find string files inside $APP
    xml2csv {FILE}  -> convert android xml file {FILE} to CSV
    csv2xml {FILE}  -> convert CSV file to XML
    """.trim())
    System.exit(1)
    error("")
}


fun main(args: Array<String>) {
    when (args.firstOrNull() ?: DEFAULT) {
        "files" -> findFiles(APP).printList("files")
        "xml2csv" -> {
            val file = args.getOrNull(1) ?: androidstrings.usage()
            val map = parseAndroidStringFile(file).printMap("strings")
            krangleStrings(map, APP)
        }
        "csv2xml" -> {
            val file = args.getOrNull(1) ?: androidstrings.usage()
            val columns = arrayOf("name", "PT", "TT")
            val df = krangleParse(readableFile(file), columns)
            writeAndroidXml(df, columns, APP + "/app/src/main/res/")
        }
        else -> usage()
    }

}


fun krangleParse(file: File, columns: Array<String>): DataFrame {
    val df = DataFrame.fromCSV(file, CSVFormat.DEFAULT.withHeader().withDelimiter(';'))
            .select(*columns)
            .filter { it["PT"].asStrings().map { it?.startsWith("XXX") == false }.toBooleanArray() }
    df.glimpse()
    return df
}

fun writeAndroidXml(df: DataFrame, columns: Array<String>, res: String) {
    val resDir = readableFile(res, directory = true)
    val langs = columns.sliceArray(1 until columns.size).map { it.toLowerCase() }
    for (l in langs) {
        val output = resDir.resolve("values-$l/strings.xml").also { it.mkdirs() ; it.debug("file $l") }

    }
    TODO("do conversion here") //To change body of created functions use File | Settings | File Templates.
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