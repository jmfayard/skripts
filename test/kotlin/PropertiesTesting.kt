import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.FreeSpec
import java.math.BigInteger
import java.util.Random

/*
 http://fsharpforfunandprofit.com/posts/property-based-testing-2/

 */

class PropertiesTesting : FreeSpec() { init {

    "Different paths, same destination" - {
        fun List<Int>.mysort() = this.sorted()
//        fun List<Int>.mysort() = this.filterNot { it == 10 }.sorted()
//        fun List<Int>.mysort() = emptyList<Int>()
//        fun List<Int>.mysort() = this.reversed()

        val gen = Gen.list(Gen.choose(8, 267))

        "Size don't change" {
            forAll(gen) { list ->
                list.mysort().size == list.size
            }
        }

        "Pairs are ordered" {
            forAll(gen) { list ->
                list.mysort().fold(Int.MIN_VALUE) { previous, value ->
                    if (value < previous) return@forAll false
                    value
                }
                true
            }
        }

        "Shuffling order makes no difference" {
            forAll(gen) { list ->
                list.reversed().mysort() == list.mysort()
            }
        }

        "Adding one to all elements" {
            fun List<Int>.increment() = this.map { it + 1 }
            forAll(gen) { list ->
                list.mysort().increment() == list.increment().mysort()
            }
        }

        "Using the sortedness of the list" {
            forAll(gen) { list ->
                (list + Int.MIN_VALUE).mysort() == listOf(Int.MIN_VALUE) + list.mysort()
            }
        }

        "Negate then sort" {
            fun List<Int>.negate() = this.map { -it }
            forAll(gen) { list ->
                list.mysort().negate() == list.negate().mysort().reversed()
            }
        }

        "There and back again" {
            forAll(gen) { list ->
                list.reversed().reversed() == list
            }
        }

        "Idempotence" {
            forAll(gen) { list ->
                list.mysort() == list.mysort().mysort()
            }
        }
    }

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
        forAll { t: String ->
            val s = t.take(7) // very few palindroms for large strings
            if (s.isPalindrome()) {
                found++
                s.reversed().isPalindrome()
            } else {
                !s.reversed().isPalindrome()
            }
        }
        println("Foupnd $found palindromes")
    }

    "Property: three characters" {
        forAll(Gen.char(), Gen.char()) { a, b ->
            "$a$b$a".isPalindrome() && "$a$b$b$a".isPalindrome()
        }
    }

    "Hard to prove, easy to verify" {
        val gen = Gen.list(Gen.choose(1, 100))
        fun String.parseCommas(): List<String> = this.split(",")

        forAll(gen) { list ->
            val string = list.joinToString(separator = ",")
            string.parseCommas() == list.map { it.toString() }
        }
    }

    "Dollars" - {
        val gen = Gen.choose(0, 10000)
        "inverses" {
            forAll(gen) { times ->
                (3 * times).dollar == 3.dollar * times
            }
        }

        "data class" {
            forAll(gen) { amount ->
                amount.dollar == amount.dollar
            }
        }
    }
}
}

fun String.isPalindrome(): Boolean {
    for (i in 0 until length) {
        if (get(i) != get(length - i - 1)) return false
    }
    return true
}

fun Gen.Companion.char() = object : Gen<Char> {
    val random = Random()

    private fun Random.nextPrintableChar(): Char {
        val low = 33
        val high = 127
        return (nextInt(high - low) + low).toChar()
    }

    override fun generate(): Char = random.nextPrintableChar()
}

val Int.bigint: BigInteger
    get() = BigInteger.valueOf(this.toLong())

data class Dollar(val amount: Int) {
    operator fun plus(amount: Int) = Dollar(this.amount + amount)
    operator fun times(times: Int) = Dollar(this.amount * times)
}

val Int.dollar
    get() = Dollar(this)
