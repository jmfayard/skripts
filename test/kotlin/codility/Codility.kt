package codility

import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.tables.row


class Codility : FreeSpec({
    "twoOfN" {
        twoOfN(4) shouldBe 6
        twoOfN(1) shouldBe 0
    }
    
    "grouping" {
        val groups = listOf(3, 5, 6, 3, 3, 5).groupSimilarValues()
        groups shouldBe mapOf(3 to 3, 5 to 2, 6 to 1)
    }
    
    "bulbs" {
        forall(
            row(0, listOf(1, 2, 3, 4)),
            row(6, listOf(1, 1, 1,1)),
            row(2, listOf(1, 1, 2, 2)),
            row(4, listOf(3, 5, 6, 3, 3, 5))
        ) { expected: Int, input: List<Int> ->
            solution(input.toIntArray()) shouldBe expected
        }
    }
})
