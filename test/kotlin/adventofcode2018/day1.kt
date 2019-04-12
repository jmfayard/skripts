package adventofcode2018.day1

import adventofcode2018.aocReadInput
import java.math.BigInteger

fun main(args: Array<String>) {
    val numbers = aocReadInput("day1.txt")
        .map { it.toInt() }
    println(numbers.sum())
    
    val testInput = listOf(+7, +7, -2, -7, -4)
    println(intermediateSums(numbers))
    
}

fun intermediateSums(input: List<Int>): List<Int> {
    var sum = 0
    val found = mutableSetOf<Int>()
    while(true) {
       for (n in input) {
           sum += n
           if (sum in found) {
               TODO("WINNER: $sum")
           }
//           println(sum)
           found += sum
       }
    }
    
}


fun findDuplicates(seq: Sequence<Int>) : BigInteger {
    var found = setOf<BigInteger>()
    var sum = BigInteger.ZERO
    for (n in seq) {
        sum += n.toBigInteger()
        if (sum in found) return sum
        found += sum
    }
    println(found.sorted())
    return  BigInteger.ZERO
}

fun seq(input: List<Int>) : Sequence<Int> {
    return sequence {
        repeat(100) {
            yieldAll(input)
        }
    }
}
