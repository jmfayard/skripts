#!/usr/bin/env kotlin-script.sh
package android

import debug
import environmentVariable
import jmfayard.*
import krangl.*
import org.apache.commons.csv.CSVFormat
import org.docopt.Docopt
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Namespace
import java.io.File


private val HELP =
        """
Kotlin scripts for Android devs.

Usage:
  android.kt layout <file>
  android.kt strings
  android.kt xml2csv <file>
  android.kt csv2xml <file>
  android.kt pseudolocale <file>
  android.kt -h | --help
  android.kt --version

Options:
  -h --help     Show this screen.
  --version     Show version.
""".trim()

private val APP : String by environmentVariable("Path to the android project you are working on")



fun main(args: Array<String>) {
    val p: Map<String, Any> = Docopt(HELP).withVersion("0.1").parse(args.toList())
    val file = p["<file>"] as? String

    when {
        p["layout"] == true -> println(extractIdsFromLayout(file!!))
        p["strings"] == true -> findFiles(APP).printList("strings")
        p["csv2xml"] == true -> i18nCsv2xml(file!!, "$APP/app/src/main/res", listOf("pt", "tdt"))
        p["xml2csv"] == true -> krangleStrings(parseAndroidStringFile(file!!).printMap("strings"), APP)
        p["pseudolocale"] == true -> pseudoLocale(parseAndroidStringFile(file!!))
    }

//        p["settings"] == true && p["<value>"] == null -> println("Show settings")
//        p["settings"] == true -> println("SET " + p["<key>"] + " = " + p["<value>"])
//android.kt settings
//android.kt settings <key> <value>

}

fun pseudoLocale(androidStringFile: Map<String, String>) {
    val map = androidStringFile.mapKeys { entry -> entry.key }
    val document = generateAndroidXml(map)
    document.printXml()
//    document.writeXmlToFile(destination)
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