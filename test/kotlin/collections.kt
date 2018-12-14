package collections

import io.kotlintest.matchers.shouldBe
import kotlin.reflect.jvm.internal.impl.protobuf.ByteString

fun main(args: Array<String>) {
    
    val list = listOf(1, 2, 3)
        .map { it }
        .mapIndexed { index, nb -> nb }
        .filter { true }
        .filterNotNull()
        .mapNotNull { it }
        .filterNot { it != 5 }
        
    list.shuffled().sorted() shouldBe list
    list.firstOrNull() 
    list.first()
    list.last()
    list.lastOrNull()
    list.random()
    list.count()
    list.distinct()
    
    list.toMutableList()
    
    val topics = mutableSetOf("kotlin", "android", "testing")
    topics += "java"
    topics += "kotlin"
    topics.toList()
    topics.toMutableList()
    
    val bytes: ByteArray = byteArrayOf(72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100, 13, 10)
    ByteString.copyFrom(bytes).toStringUtf8() shouldBe "Hello World"
    
    
}

fun fibRec(n: Int):Int {
    return if (n in 0..1) 1 else fibRec(n - 1) + fibRec(n-2)
}

fun fibonacciSeq(): Sequence<Int> = sequence {
    var i = 0
    while(true) {
        yield(fibRec(i))
        i++
    }
}
