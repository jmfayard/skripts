#!/usr/bin/env kotlin-script.sh
package androidstrings

import jmfayard.*
import krangl.*
import org.apache.commons.csv.CSVFormat
import org.jdom2.Document
import org.jdom2.Element
import java.io.File

private val DEFAULT = "csv2xml"
private val APP = "/Users/jmfayard/Dev/mautinoa/mautinoa-app"
private fun usage(): Nothing {
    println(
        """
$ androidstrings.kt COMMAND OPTIONS

Convert localisations between the android xml format and CSV (thus excel)

COMMAND can be
    help            -> print usage
    files           -> find string files inside $APP
    xml2csv {FILE}  -> convert android xml file {FILE} to CSV
    csv2xml {FILE}  -> convert CSV file to XML
    """.trim()
    )
    System.exit(1)
    error("")
}


fun main(args: Array<String>) {
    when (args.firstOrNull() ?: DEFAULT) {
        "files" -> findFiles(APP).printList("files")
        "xml2csv" -> {
            val file = args.getOrNull(1) ?: usage()
            val map = parseAndroidStringFile(file).printMap("strings")
            krangleStrings(map, APP)
        }
        "csv2xml" -> {
            if (args.size < 2) usage()
            i18nCsv2xml(args[1], "$APP/app/src/main/res", listOf("pt", "tdt"))
        }
        else -> usage()
    }

}

fun i18nCsv2xml(srcPath: String, destPath: String, langs: List<String>) {
    val destDir = readableFile(destPath, directory = true)
    val df = krangleParse(readableFile(srcPath), langs)
    for (lang in langs) {
        val i18nMap: Map<String, String> = i18nStrings(df, lang)
        val destination = destDir.resolve("values-$lang/strings.xml")
        val document = generateAndroidXml(i18nMap)
        document.printXml()
        document.writeXmlToFile(destination)
    }
}


fun krangleParse(file: File, langs: List<String>): DataFrame {
    val lang = langs.first()
    val columns = arrayOf("name") + langs
    val df = DataFrame.fromCSV(file, CSVFormat.DEFAULT.withHeader().withDelimiter(';'))
        .select(*columns)
        .filter { it[lang].asStrings().map { it?.startsWith("XXX") == false }.toBooleanArray() }
    df.glimpse()
    return df
}

fun i18nStrings(df: DataFrame, lang: String): Map<String, String> =
    df.rows.associate { it["name"] as String to it[lang] as String }


fun generateAndroidXml(i18nMap: Map<String, String>): Document =
    xmlDocument("resources") {
        for ((key, value) in i18nMap) {
            addElement("string", mapOf("name" to key)) {
                text = value
            }
        }
    }


fun findFiles(path: String): List<File> {
    val base = readableFile(path, directory = true)
    val files = base.walkTopDown().filter { it.name == "strings.xml" }
    return files.toList()
}

fun parseAndroidStringFile(path: String): Map<String, String> {
    val document = parseXmlFile(path)
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