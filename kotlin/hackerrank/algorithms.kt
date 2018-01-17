package hackerrank

import org.junit.Test


fun stdin(): List<String> {
    if (System.`in`.available() == 0) {
        System.err.println("nothing in stdin"); System.exit(1)
    }
    return generateSequence { readLine() }.toList()
}

inline fun <T> Iterable<T>.sumLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

private fun String.toInts() = split(" ").map { it.toIntOrNull() }.filterNotNull()
private fun String.toLongs() = split(" ").map { it.toLongOrNull() }.filterNotNull()


typealias Input = List<String>

fun main(args: Array<String>) {
    val (n, _) = readLine()!!.toInts()
    val input = generateSequence { readLine() }
    println(addAndFindMax(n, input).max()!!)
}

// https://www.hackerrank.com/challenges/crush/problem fails with timeout
fun addAndFindMax(n: Int, lines: Sequence<String>): List<Long> {
    val current = MutableList(n + 1) { 0L }
    for (line in lines) {
        val (from, to, add) = line.toInts()
        for (index in from..to) {
            current[index] = current[index] + add
        }
    }
    return current
}

fun simpleLongSum(lines: Input): Long {
    val longs = lines[1].split(" ").map { it.toLongOrNull() }.filterNotNull()
    return longs.fold(0L, { sum, next -> sum + next })
}

fun simpleArraySum(lines: List<String>): Int {
    return lines[1].split(" ").sumBy { it.toInt() }
}

class AlgorithmTests {

    @Test
    fun max() {
        val input = listOf(
            "1 2 001",
            "2 3 010",
            "3 4 100"
        )
        val expected = listOf(0, 1, 11, 110, 100, 0).map { it.toLong() }
        addAndFindMax(5, input.asSequence()).toList() shouldBe expected
    }

    @Test
    fun longSum() {
        val input = listOf("5", "1000000001 1000000002 1000000003 1000000004 1000000005")
        simpleLongSum(input) shouldBe 5000000015
    }

    @Test
    fun testSimpleArray() {
        val lines = listOf("6", "1 2 3 4 10 11")
        simpleArraySum(lines) shouldBe 31
    }
}


infix fun <T> T?.shouldBe(expected: Any?) {
    if (this != expected) error("ShouldBe Failed!\nExpected: $expected\nGot:      $this")
}