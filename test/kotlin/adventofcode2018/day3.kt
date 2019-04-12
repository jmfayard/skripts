package adventofcode2018.day3

import adventofcode2018.aocReadInput

fun main() {
    val lines = aocReadInput("day3.txt")
    val areas = lines.map { area(it) }
    val (width, height) = areas.dimension()
    val matrix = Matrix(width, height)
    for (a in areas) {
        matrix.paintArea(a)
    }
    matrix.print()
    val solutions = areas.map { it.n } - matrix.collisions
    println("solutions=$solutions")
}

private class Matrix(val width: Int, val height: Int) {
    operator fun set(left: Int, top: Int, value: Int) {
        data[left * width + top] = value
    }

    operator fun get(left: Int, top: Int): Int {
        return data[left * width + top]
    }

    val size = width * height
    val data = MutableList(size) { 0 }

    var collisions = mutableSetOf<Int>()

    fun paintArea(a: Area) {
//        println(a)
        for (i in a.left.until(a.left + a.width)) {
            for (j in a.top.until(a.top + a.height)) {
                val existing = this[i, j]
                this[i, j] = if (existing == 0) {
                    a.n
                } else {
                    collisions.add(a.n)
                    collisions.add(existing)
                    -1
                }
            }
        }
//        this.print()
    }

    fun print() {
        for (i in 0.until(width)) {
            println()
            for (j in 0.until(height)) {
                print(this[i, j])
            }
        }
        println()
    }
}

private data class Area(val id: String, val left: Int, val top: Int, val width: Int, val height: Int) {
    val n = id.removePrefix("#").toInt()
}

private fun area(line: String) : Area {
    val (id, _, corner, size) = line.split(' ')
    val (left, top) = corner.removeSuffix(":").split(',').map { it.toInt() }
    val (width, height) = size.split('x').map { it.toInt() }
    return Area(id, left, top, width, height)
}
private fun List<Area>.dimension(): Pair<Int, Int> {
    val width = this.map { it.left + it.width }.max() ?: 0
    val height = this.map { it.top + it.height }.max() ?: 0
    return Pair(width, height)
}


