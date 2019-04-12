package adventofcode2018.day2

import adventofcode2018.aocReadInput

fun main() {
    val list = aocReadInput("day2.txt").map { line ->
        aocSimilarChars(line)
    }
    val pairs = list.filter { it.first }.count()
    val triples = list.filter { it.second }.count()
    println("pairs=$pairs triples=$triples checksum=${pairs*triples}")
    
    computeDiffs(aocReadInput("day2.txt"))
}

fun aocSimilarChars(s: String): Pair<Boolean, Boolean> {
    val chars = s.groupBy { it }
    val hasPair = chars.values.any { it.size == 2 }
    val hasTriple = chars.values.any { it.size == 3 }
    return Pair(hasPair, hasTriple)
}

fun computeDiffs(lines: List<String>) {
    for (l in lines) {
        for (m in lines) {
            val diff = lineDifference(l, m)
            if (diff == 1) println("l=$l m=$m diff=$diff")
        }
    }
}

fun lineDifference(l: String, m: String): Int {
    check(l.length == m.length) { "Invalid lines [$l] [$m]" }
    val transform = { c: Char, d: Char -> c == d }
    return l.zip(m, transform).count { it.not() }
}
