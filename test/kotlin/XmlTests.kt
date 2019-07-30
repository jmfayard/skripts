import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import jmfayard.addElement
import jmfayard.walk
import jmfayard.xmlDocument

class XmlTests : StringSpec() { init {

    val doc = xmlDocument("adam") {
        val children = listOf("yann", "eve", "eudes", "ubert")
        children.forEachIndexed { i, child ->
            addElement(child) {
                addElement("gc-$child-1") { }
                addElement("gc-$child-2") { }
            }
        }
    }

    "walk" {
        val expected =
            "yann, eve, eudes, ubert, gc-yann-1, gc-yann-2, gc-eve-1, gc-eve-2, gc-eudes-1, gc-eudes-2, gc-ubert-1, gc-ubert-2".split(
                ", "
            )
        doc.walk().map { it.name } shouldBe expected
    }

    "walk depthFirst" {
        val expected =
            "yann, gc-yann-1, gc-yann-2, eve, gc-eve-1, gc-eve-2, eudes, gc-eudes-1, gc-eudes-2, ubert, gc-ubert-1, gc-ubert-2".split(
                ", "
            )
        doc.walk(depthFirst = true).map { it.name } shouldBe expected
    }

    "walk skipRoot, level=1" {
        val expected = "yann, eve, eudes, ubert".split(", ")
        doc.walk(level = 1).map { it.name } shouldBe expected
    }

    "walk level=1, skipRoot=false" {
        val expected = "adam, yann, eve, eudes, ubert".split(", ")
        doc.walk(level = 1, skipRoot = false).map { it.name } shouldBe expected
    }
}
}
