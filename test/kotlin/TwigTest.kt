import io.kotlintest.matchers.haveSubstring
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
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

            template.render(model).trim() should haveSubstring(expected.trim())
        }
    }
}

data class TwigPresentation(val title: String, val speakerName: String)
