import io.kotlintest.specs.StringSpec

data class Point(val x: Int, val y: Int)

operator fun Point.plus(other: Point) = Point(this.x + other.x, this.y + other.y)
operator fun Point.plus(n: Int) = Point(x + n, y + n)

operator fun Point.minus(other: Point) = Point(this.x - other.x, this.y - other.y)
operator fun Point.minus(n: Int) = Point(x - n, y - n)

operator fun Point.times(n: Int) = Point(n * x, n * y)

operator fun Point.div(n: Int): Point = Point(x / n, y / n)

operator fun Point.compareTo(b: Point): Int = when {
    this.x > b.x && this.y > b.x -> -1
    this.x > b.x && this.y > b.x -> 1
    else -> 0
}


private operator fun String.minus(regex: Regex): String
        = replace(regex, "")


class OperatorTest : StringSpec() {init {
    val a = Point(1, 3)
    val b = Point(4, 5)

    ".plus() && .minus()" {
        a + b shouldBe Point(5, 8)
        a + 1 shouldBe Point(2, 4)
        a - b shouldBe Point(-3, -2)
        a - 1 shouldBe Point(0, 2)
    }

    ".times() && .div()" {
        a * 2 shouldBe Point(2, 6)
        a / 2 shouldBe Point(0, 1)
    }

    "remove spaces " {
        val spaces = Regex("\\s+")
        " abc   e  f " - spaces shouldBe "abcef"
    }

    ".compareTo()" {
        (a <= b) shouldBe true
    }
}
}






