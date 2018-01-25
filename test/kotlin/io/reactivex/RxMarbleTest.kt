package io.reactivex

import io.kotlintest.specs.FreeSpec
import io.reactivex.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

class RxMarbleTest : FreeSpec() { init {

    val rxMarble = RxMarble(tickMs = 200L)
    val empty = emptyList<Int>()



    "error cases" - {
        val invalidMarbles = listOf(
                "--a-b---c--", " 10-", "-0-1-%-",
                "---|#", "--||", "---##", "--^^",
                "-^--" // not allowed for cold
        )
        for (marble in invalidMarbles) {
            "marble: $marble" {
                shouldThrow<IllegalArgumentException> {
                    rxMarble.checkMarbles(marble, hot = false)
                }
            }
        }

    }

    "parseNumbers" {
        val empty = emptyList<Int>()
        rxMarble.parseNumbers("-^--1-3--3--1|") shouldBe listOf(1, 3)
        rxMarble.parseNumbers("-^--1-2-10-3-|") shouldBe listOf(1, 2, 3, 10)
        rxMarble.parseNumbers("|") shouldBe empty
        rxMarble.parseNumbers("#") shouldBe empty
    }

    "invalid mapping" - {
        val mapping = listOf("a", "b", "c")

        "index start at one, not zero" {
            shouldThrow<IllegalArgumentException> {
                rxMarble.cold("0--1-2", mapping)
            }
        }

        "out of band" {
            shouldThrow<IllegalArgumentException> {
                rxMarble.cold("--1-2--3---4", mapping)
            }
        }

        "mapping function crash" {
            shouldThrow<IllegalArgumentException> {
                rxMarble.cold("--1-2-3-4", { mapping[it] })
            }
        }
    }

    "marbleOf(cold()" - {
        val ints = listOf(2, 4, 6, 8, 10)
        val e = listOf(4, 6, 8)
        val tests = mapOf(
                "--1----2---3-|----------------" to e,
                "--1----2---3-#----------------" to e,
                "-#----------------------------" to empty,
                "-|----------------------------" to empty,
                "|-----------------------------" to empty,
                "------------------------------" to empty,
                "----1---2--3------------------" to e
        )
        for ((input, value) in tests) {
            "Marble $input" {
                val (output, actual) = rxMarble.marbleOf(rxMarble.cold(input, ints))
                output shouldBe input
                actual shouldBe value
            }
        }
    }

    "rxMarble" {

        "never" {
            rxMarble.marbleOf(Observable.never<Int>()).also { (marble, values) ->
                marble shouldBe "------------------------------"
                values shouldBe empty
            }

        }

        "just" {
            rxMarble.marbleOf(Observable.just(2)).also { (marble, values) ->
                marble shouldBe "1|----------------------------"
                values shouldBe listOf(2)
            }
        }


    }

}
}