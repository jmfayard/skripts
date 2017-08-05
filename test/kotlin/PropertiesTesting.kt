import com.squareup.moshi.Moshi
import io.kotlintest.properties.Gen
import io.kotlintest.specs.StringSpec
import java.util.*

/*
 http://fsharpforfunandprofit.com/posts/property-based-testing-2/

 */



fun String.isPalindrome() : Boolean {
    for (i in 0 until length) {
        if (get(i) != get(length-i-1)) return false
    }
    return true
}

object GenChar : Gen<Char> {
    val random = Random()

    private fun Random.nextPrintableChar(): Char {
        val low = 33
        val high = 127
        return (nextInt(high - low) + low).toChar()
    }
    override fun generate(): Char = random.nextPrintableChar()

}

class PropertiesTesting : StringSpec() { init {


    "Property: it never crashes" {
        forAll { s: String ->
            try {
                s.isPalindrome()
                true
            } catch (e: Exception) {
                false
            }

        }
    }

    "Property: if s is a  palindrome, s.inverse() is one too" {
        var found = 0
        forAll { t : String ->
            val s = t.take(7) // very few palindroms for large strings
            if (s.isPalindrome()) {
                println("Found $s")
                s.reversed().isPalindrome()
            } else {
                !s.reversed().isPalindrome()
            }
        }
    }

    "Property: three characters" {
        forAll(GenChar, GenChar) { a, b ->
            "$a$b$a".isPalindrome() && "$a$b$b$a".isPalindrome()
        }
    }

}}


