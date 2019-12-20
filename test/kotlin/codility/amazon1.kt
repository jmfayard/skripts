package codility.amazon1


fun main() {
    input("CAGCCTA").min("250", "456")
}

data class Input(
    val size: Int,
    val source: String,
    val input: List<Int>,
    val whereIsA: List<Int>
) {
    fun print(): Input = log("input", source)
}

val ints: Map<Char, Int> = List(10) { it.toString().first() to it }.toMap().log("ints")

fun Input.min(p: String, q: String): List<Int> {
    val pInt = p.map { e -> ints[e]!! }
    val qInt = q.map { e -> ints[e]!! }
    return min(pInt, qInt).log("min", source)
}

fun Input.min(p: List<Int>, q: List<Int>): List<Int> {
    require(p.size == q.size) { "Invalid\np=$p\nq=$q" }
    return List(p.size) { i ->
        min(p[i], q[i])
    }
}


fun Input.min(p: Int, q: Int): Int {
    var max = 4
    for (i in p..q) {
        max = Math.min(max, input[i])
    }
    return max
}


fun input(source: String): Input {
    val impacts = mapOf('A' to 1, 'C' to 2, 'G' to 3, 'T' to 4)
    require(source.all { it in impacts.keys }) { "Invalid source=$source" }
    val size = source.length
    val input = source.map { impacts[it]!! }
    val whereIsA = source.map { 4 }.toMutableList()
    var current = 4
    for (i in input.indices.reversed()) {
        if (input[i] == 1) current = i
        whereIsA[i] = current
    }
    return Input(size, source, input, whereIsA).print()
}

val DEBUG = true
fun <T> T.log(key: String, bracket: String = ""): T {
    if (DEBUG) println("$key[$bracket]=$this")
    return this
}



