import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.kotlintest.properties.Gen
import io.kotlintest.specs.StringSpec

data class Pizza(val name: String, val price: Int)

fun Gen.Companion.pizza() = object : Gen<Pizza> {
    override fun generate() = Pizza(Gen.string().generate(), Gen.int().generate())

}

class MoshiProperties : StringSpec() { init {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(Pizza::class.java)

    "Property: serialize and unserialize" {

        forAll(Gen.pizza()) { pizza ->
            adapter.fromJson(adapter.toJson(pizza)) == pizza
        }
    }

    "When no escaping is present" {
        forAll { i: Int ->

            val string = """{"name": "$i", "price": $i}"""
            try {
                adapter.fromJson(string) == Pizza("$i", i)
            } catch (e: Exception) {
                true
            }
        }
    }

}
}
