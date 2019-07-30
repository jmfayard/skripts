#!/usr/bin/env kotlin-script.sh
package android

import debug
import jmfayard.addElement
import jmfayard.parseXmlFile
import jmfayard.printList
import jmfayard.printMap
import jmfayard.printXml
import jmfayard.readableFile
import jmfayard.walk
import jmfayard.writableFile
import jmfayard.writeXmlToFile
import jmfayard.xmlDocument
import krangl.DataFrame
import krangl.asStrings
import krangl.dataFrameOf
import krangl.print
import krangl.readCSV
import krangl.writeCSV
import org.apache.commons.csv.CSVFormat
import org.docopt.Docopt
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import java.io.File
import java.util.regex.Pattern

private val CSV_DELIMITOR = ';'
private val VERSION = "0.3"
val RES_DIR = listOf("src/main/res_shared", "app/src/main/res_shared", "app/src/main/res", "src/main/res")
private val HELP =
    """
Kotlin scripts for Android devs.

Usage:
  android.kt layout <file>
  android.kt strings
  android.kt xml2csv --dest <csv> <lang>...
  android.kt csv2xml --src <csv> <lang>...
  android.kt pseudolocale <file>
  android.kt -h | --help
  android.kt --version

Options:
  --module <dir>    Path to the android module
  -h --help         Show this screen.
  --version         Show version.
""".trim()

fun main(args: Array<String>) {
    val p: Map<String, Any> = Docopt(HELP).withVersion(VERSION).parse(args.toList())
    val file = p["<file>"] as? String
    val module = p["<dir>"] as? String ?: "."

    when {
        p["layout"] == true -> println(extractIdsFromLayout(file!!))
        p["strings"] == true -> findFiles(module).printList("strings.xml")
        p["csv2xml"] == true -> convertI18nFilesCsvToAndroid(p, module)
        p["xml2csv"] == true -> convertI18nFilesAndroid2Csv(p, module)
        p["pseudolocale"] == true -> pseudoLocale(parseAndroidStringFile(file!!))
    }
}

//   android.kt xml2csv --module <dir> --dest <csv> <lang>...
fun convertI18nFilesAndroid2Csv(p: Map<String, Any>, module: String) {
    println(p)
    val dest = p["<csv>"] as? String ?: error("Missing argument --dest output.csv")
    val locales = p["<lang>"] as? List<String> ?: error("Missing locales [fr en pt]")
    convertI18nFilesAndroid2Csv(module, writableFile(dest), locales)
}

/** android.kt csv2xml --module <dir> --src <csv> <lang>... **/
fun convertI18nFilesCsvToAndroid(p: Map<String, Any>, module: String) {
    println(p)
    val src = p["<csv>"] as? String ?: error("Missing argument --src output.csv")
    val locales = p["<lang>"] as? List<String> ?: error("Missing locales [fr en pt-rTL]")
    val resDir = resDirectory(File(module))

    i18nCsv2xml(readableFile(src), resDir, locales)
}

fun resDirectory(module: File): File {
    return RES_DIR.map { module.resolve(it) }.firstOrNull { it.isDirectory }
        ?: error("Invalid android repository ${module.absolutePath}")
}

fun convertI18nFilesAndroid2Csv(module: String, output: File, locales: List<String>) {

    val RES = resDirectory(File(module))

    val english = parseAndroidStringFile(RES.resolve("values/strings.xml").absolutePath).printMap("strings")
    val others = locales.associate { code ->
        val localeFile = RES.resolve("values-$code/strings.xml").absolutePath
        code to parseAndroidStringFile(localeFile)
    }
    krangleStrings(english, output, others)
}

fun krangleStrings(english: Map<String, String>, dest: File, locales: Map<String, Map<String, String>>) {
    val codes = locales.keys.sorted()
    val rows = listOf("name", "en") + codes
    val values = english.keys
        .sorted()
        .flatMap { key ->
            val translations = codes.map { locales[it]!!.getOrDefault(key, english[key]) }
            listOf(key, english[key]) + translations
        }
        .toTypedArray()
    val df: DataFrame = dataFrameOf(*rows.toTypedArray())(*values)
    df.print()
    df.writeCSV(dest, format = CSVFormat.EXCEL.withDelimiter(CSV_DELIMITOR))
    println("Written to ${dest.absolutePath}")
}

fun pseudoLocale(androidStringFile: Map<String, String>) {
    val map = androidStringFile.mapValues { keyWithPlaceholder(it.key, it.value) }
    val document = generateAndroidXml(map)
    document.printXml()
//    document.writeXmlToFile(destination)
}

private val regexpPH = Pattern.compile("(%s|%\\d\\\$s)")

fun keyWithPlaceholder(key: String, value: String): String {
    val placeHoldersNumber = value.split(regexpPH).size - 1
    var valueWithPlaceHodler = key
    repeat(placeHoldersNumber) {
        valueWithPlaceHodler += " %s"
    }
    return valueWithPlaceHodler
}

fun extractIdsFromLayout(path: String): String {
    val layoutFile = readableFile(path).debug("layoutFile")
    val pathId = "R.layout." + layoutFile.nameWithoutExtension
    val layoutElements = parseXmlFile(layoutFile).walk(skipRoot = false)
        .map(Element::asLayoutElement)
        .filter { it.id.isNotBlank() }

    val enums = layoutElements.joinToString(separator = "\n") { e: LayoutElement ->
        "    " + e.suggestedName + " (" + e.androidId() + "), // " + e.type
    }

    return """
interface HasId {
    val id: Int
}

enum class MyEnum(override val id: Int) : HasId {
$enums
    Layout($pathId) // Layout ${layoutFile.name}
}
    """.trimIndent()
}

private fun Element.asLayoutElement(): LayoutElement {
    val id = this.getAttribute("id", NAMESPACE_ANDROID)?.let { attribute ->
        attribute.value.removePrefix("@").removePrefix("+").removePrefix("id/")
    } ?: ""
    return LayoutElement(this.name, id)
}

val NAMESPACE_ANDROID = Namespace.getNamespace("http://schemas.android.com/apk/res/android")

private data class LayoutElement(val type: String, val id: String) {
    val suggestedName = camelCaseOf(id)

    fun androidId() = "R.id.$id"

    fun camelCaseOf(name: String): String {
        return name.split("_")
            .map { it.capitalize() }
            .joinToString(separator = "")
    }
}

fun i18nCsv2xml(csvFile: File, destDir: File, langs: List<String>) {
    val df = krangleParse(csvFile, langs)
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
    val df = DataFrame.readCSV(file, CSVFormat.DEFAULT.withHeader().withDelimiter(CSV_DELIMITOR))
        .select(*columns)
        .filter { it[lang].asStrings().map { it?.startsWith("XXX") == false }.toBooleanArray() }
    return df
}

fun i18nStrings(df: DataFrame, lang: String): Map<String, String> =
    df.rows.associate { it["name"] as String to it[lang] as String }

fun generateAndroidXml(i18nMap: Map<String, String>): Document =
    xmlDocument("resources") {
        for ((key, value) in i18nMap) {
            addElement("string", mapOf("name" to key)) {
                text = espaceXmlValue(value)
            }
        }
    }

fun espaceXmlValue(value: String): String {
    return value.replace("'", "\\'")
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
