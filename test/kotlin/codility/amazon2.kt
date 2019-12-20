package codility.amazon2


fun main() {
    val input = input(intArrayOf(1, 3, 4, 2, 2, 2, 1, 1, 2))
    input.findSolution()
}
data class Ship(val N: Int, val S: String, val T: String)

val ship1 = Ship(3, "1B, 2C, 2D 4D", "2B 2D 3D 4D 4A")
val ship2 = Ship(3, "1A 1B,2C 2C", "1B")
fun hackathonMode(ship: Ship) : String = when(ship) {
    ship1 -> "1,1"
    ship2 -> "0,1"
    else -> "Not implemented yet"
}

fun Input.findSolution(): Triple<Int, Int, Int>? {
    val sums = fromStart.filter { it in fromEnd }.log("sums")
    val triples = sums.map { sum -> 
        val start = 1 + fromStart.indexOfFirst { it == sum }
        val end = A.size - 2 - fromEnd.indexOfFirst { it == sum }
        Triple(start, end, sum)
    }.log("triples")
    val okIndexes = 1..(A.size-2)
    return triples.firstOrNull { 
        val (start, end, sum) = it
        when {
            start !in okIndexes || end !in okIndexes -> false
            else -> fromStart[end-1] - fromStart[start] == sum
        }
    }.log("solution")
}


data class Input(
    val totalSum: Int,
    val A: List<Int>,
    val fromStart: List<Int>,
    val fromEnd: List<Int>
)

fun input(source: IntArray): Input {
    val A = source.toList().log("A")
    val totalSum = A.sum()
    val fromStart = A.cumulative().log("fromStart")
    val fromEnd = A.reversed().cumulative().log("fromEnd")
    return Input(totalSum, A, fromStart, fromEnd)
}



//
//fun Input.isValid(a: Int, b: Int): Boolean {
//    val sum1 = A.take(a).sum()
//    val sum3 = A.filterIndexed { index, i -> index > b }.sum()
//    val sum2 = A.filterIndexed { index, i -> index in (a + 1) until b }.sum()
//    val res = sum1 == sum2 && sum2 == sum3
//    return res.log("valid", "$sum1.$sum2.$sum3")
//}
//
//fun Input.indexOfSum(sum: Int) : Pair<Int, Int>? {
//    val start = A.indices.firstOrNull { i -> fromStart[i] == sum  }
//    val end = A.indices.firstOrNull { i -> fromStart[i] + sum == totalSum }
//    return if (start != null && end != null) Pair(start, end) else null
//}
//
//fun choosetwo(n: Int) : List<Pair<Int, Int>> {
//    val indices = List(n) { it }
//    return indices.flatMap { i -> 
//        indices
//            .filter {  j -> j > i }
//            .map { j -> Pair(i, j) }
//    }.log("choosetwo")
//} 
//
//fun Input.findSolution(): Pair<Int, Int>? {
//    val triples = choosetwo(candidates.size)
//        .map { Triple(it.first, it.second, (totalSum - it.first - it.second).div(3) }
//        .log("triples")
//    val found = triples.firstOrNull { (a)
//        
//    }
//}

fun List<Int>.cumulative() : List<Int> {
    var result = mutableListOf<Int>()
    var current = 0
    for (i in indices) {
        current += get(i)
        result.add(current)
    }
    return result
}

val DEBUG = true
fun <T> T.log(key: String, bracket: String = ""): T {
    if (DEBUG) println("$key[$bracket]=$this")
    return this
}



