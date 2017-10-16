import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.StringSpec



fun evilFizzBuzz(number: Int): String = when {
    number > 100 -> "FOOBAR"
    number % 3 == 0 && number % 5 == 0 -> "FizzBuzz"
    number % 3 == 0 -> "Fizz"
    number % 5 == 0 -> "Buzz"
    else -> number.toString()
}

fun realFizzBuzz(number: Int): String = when {
    number % 3 == 0 && number % 5 == 0 -> "FizzBuzz"
    number % 3 == 0 -> "Fizz"
    number % 5 == 0 -> "Buzz"
    else -> number.toString()
}


class FizzBuzzTesting : FreeSpec() { init {

    "it's always possible to trumps example-based testing" {

        val expected = listOf("FizzBuzz",
                "1", "2", "Fizz", "4", "Buzz",
                "Fizz", "7", "8", "Fizz", "Buzz",
                "11", "Fizz", "13", "14", "FizzBuzz",
                "16", "17", "Fizz", "19", "Buzz",
                "Fizz", "22", "23", "Fizz", "Buzz",
                "26", "Fizz", "28", "29", "FizzBuzz",
                "31")
        for (i in 1..expected.lastIndex) {
            evilFizzBuzz(i) shouldBe expected[i]
        }
    }

    "but no implementation can break properties-based testing" - {

        val fizzbuzz = ::realFizzBuzz
//        val fizzbuzz = ::evilFizzBuzz

        "Multiples of 15" {
            forAll<Int> { i ->
                if (Math.abs(i)  >= Integer.MAX_VALUE / 15 -1) return@forAll true
                val nb = 15 * Math.abs(i)
                fizzbuzz(nb) == "FizzBuzz"
            }
        }

        "Multiples of 3" {
            forAll<Int> { i ->
                if (Math.abs(i)  >= Integer.MAX_VALUE / 3 -1) return@forAll true
                val nb = 3 * Math.abs(i)
                nb % 5 == 0 || fizzbuzz(nb) == "Fizz"
            }
        }

        "Multiples of 5" {
            forAll<Int> { i ->
                if (Math.abs(i) >= Integer.MAX_VALUE / 5 -1) return@forAll true

                val nb = 5 * Math.abs(i)
                nb % 5 == 0 || fizzbuzz(nb) == "Fizz"
            }
        }

        "Others" {
            forAll<Int> { i ->
                val nb = Math.abs(i)
                nb % 5 == 0 || nb % 3 == 0 || fizzbuzz(nb) == nb.toString()
            }
        }

    }


} }