package codility

import java.math.BigInteger

fun List<Int>.groupSimilarValues() : Map<Int, Int> = 
    this.groupBy { it }.mapValues { it.value.count() }

fun twoOfN(n: Int) = 
    n * (n-1) / 2

fun solution(A: IntArray): Int {
    val groups = A.toList().groupSimilarValues()
    val counts = groups.map { twoOfN(it.value) }
    println(counts)
    return counts.sum()
}
