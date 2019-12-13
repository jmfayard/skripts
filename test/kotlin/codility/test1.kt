package codility

data class State(val on: List<Int>) {
    fun allLightenUp(): Boolean = when {
        on.isEmpty() -> false
        else -> on.sorted() == (1 .. on.max()!!).sorted()
    }
}
fun generateStates(input: List<Int>) = input.indices.map { n ->
    State(input.take(n+1))
}

fun solution(A: IntArray): Int {
    return generateStates(A.toList())
        .also { println(it) }
        .filter { lux -> lux.allLightenUp() }
        .also { println(it) }
        .count()
}
fun positiveNumbers() = sequence {
    var i = 1
    while(true) {
        yield(i++)
    }
}
