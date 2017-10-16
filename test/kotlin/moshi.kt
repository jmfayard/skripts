import com.squareup.moshi.JsonReader
import io.kotlintest.specs.StringSpec
import okio.Buffer
import org.intellij.lang.annotations.Language

class TestMoshi : StringSpec() { init {

    @Language("JSON") val json = """
{ "a" : [1, 2], "b" : [3,4], "c": [5, 6]}
"""

    "Parsins json with unknown keys" {
        val buffer = Buffer()
        buffer.writeUtf8(json)
        val reader = JsonReader.of(buffer)

        val map = reader.readJsonValue() as Map<String, List<Int>>
        map.keys.sorted() shouldBe listOf("a", "b", "c")
        map["a"] shouldBe listOf(1.0, 2.0)
    }

}
}