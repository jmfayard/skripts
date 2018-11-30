import io.kotlintest.matchers.HaveWrapper
import io.kotlintest.matchers.have
import io.kotlintest.specs.StringSpec
import org.jtwig.JtwigModel
import org.jtwig.JtwigTemplate

class TwigTest : StringSpec() {
    init {

        "Hello World" {
            val template = JtwigTemplate.classpathTemplate("twig/hello.twig")
            val model = JtwigModel.newModel()

            template.render(model).trim() shouldBe "Hello"

            template.render(model.with("name", "World")) shouldBe "Hello World"

            forAll<String> { name ->
                model.with("name", name)
                template.render(model) == "Hello $name"
            }
        }

        "Presentations" {
            val presentations = listOf(
                TwigPresentation("rxjava", "jake"),
                TwigPresentation("kotlin", "orangy")
            )

            val expected =
                """
    rxjava - jake
    kotlin - orangy
"""

            val template = JtwigTemplate.classpathTemplate("twig/index-jtwig.twig")
            val model = JtwigModel.newModel().with("presentations", presentations)

            template.render(model) should have text (expected)
        }
    }
}

private infix fun HaveWrapper<String>.text(text: String) {
    val actual = value.trim().replace(spaces, " ")
    val expected = text.trim().replace(spaces, " ")
    if (actual != expected) {
        throw AssertionError("Content are not identical:\nWanted =>\n$expected\nGot =>\n$actual\n")
    }
}

private val spaces = Regex("[ \\t]+")

data class TwigPresentation(val title: String, val speakerName: String)
