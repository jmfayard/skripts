package jmfayard

import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.File

fun parseXmlFile(path: String): Document =
    parseXmlFile(readableFile(path))

fun parseXmlFile(file: File): Document =
    SAXBuilder().build(file).document


fun Document.writeXmlToFile(destination: File) {
    destination.parentFile.mkdirs()
    println("Writing to ${destination.absolutePath}")
    val outputer = XMLOutputter(Format.getPrettyFormat())
    outputer.output(this, destination.outputStream())
}

fun Document.printXml() {
    val outputer = XMLOutputter(Format.getPrettyFormat())
    outputer.output(this, System.out)
}

fun Document.walk(depthFirst: Boolean = false, level: Int = Int.MAX_VALUE, skipRoot: Boolean = true): List<Element> =
    DocumentWalker(this, level, depthFirst, skipRoot).stack

class DocumentWalker(document: Document, maxLevel: Int, val depthFirst: Boolean, val skipRoot: Boolean) {
    val stack = mutableListOf<Element>()

    init {
        if (!skipRoot) stack.add(document.rootElement)
        findChildren(document.rootElement, maxLevel - 1)
    }

    fun findChildren(element: Element, level: Int) {
        if (level < 0) return
        if (depthFirst) {
            for (e in element.children) {
                stack.add(e)
                findChildren(e, level - 1)
            }

        } else {
            stack.addAll(element.children)
            for (e in element.children) {
                findChildren(e, level - 1)
            }
        }
    }

}

fun xmlDocument(
    rootElement: String,
    attributes: Map<String, String> = emptyMap(),
    configure: Element.() -> Unit
): Document {
    val elem = Element(rootElement)
    elem.attributes.addAll(attributes.map { Attribute(it.key, it.value) })
    return Document(elem).also { configure(elem) }
}

fun Element.addElement(
    name: String,
    attributes: Map<String, String> = emptyMap(),
    configure: Element.() -> Unit
): Element {
    val child = Element(name)
    child.attributes.addAll(attributes.map { Attribute(it.key, it.value) })
    child.apply(configure)
    addContent(child)
    return child
}

