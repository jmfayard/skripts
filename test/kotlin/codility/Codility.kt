package codility

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row


class Codility : FreeSpec({
    "positive numbers" {
        positiveNumbers().take(10).toList() shouldBe listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }
    
    "all on" {
        State(listOf(1, 2, 3)).allLightenUp() shouldBe true
        State(listOf(2, 4)).allLightenUp() shouldBe false
        generateStates(listOf(2, 3, 4, 1, 5)).forEach { println(it) }
    }

    "bulbs" {
        forall(
            row(3, listOf(2, 1, 3, 5, 4)),
            row(2, listOf(2, 3, 4, 1, 5))
        ) { expected: Int, input: List<Int> ->
            solution(input.toIntArray()) shouldBe expected
        }
    }
})
