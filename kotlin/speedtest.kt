#!/usr/bin/env kotlin-script.sh
package speedtest

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.*

fun main(args: Array<String>) = runBlocking {
    var result = SpeedtestResult(0, "", "", 0)
    repeat(100) { delay(10) ; result.render()  }
    result = SpeedtestResult(41, "", "", 1)
    repeat(100) { delay(10) ; result.render()  }
    result = SpeedtestResult(41, "15 Mbps", "", 2)
    repeat(100) { delay(10) ; result.render()  }
    result = SpeedtestResult(41, "15 Mbps", "", 3)
    repeat(100) { delay(10) ; result.render()  }
    result = SpeedtestResult(41, "15 Mbps", "0.8 Mbps", 4)
    repeat(100) { delay(10) ; result.render()  }
    result = SpeedtestResult(41, "15 Mbps", "0.7 Mbps", 5)
    result.render()
}



fun SpeedtestResult.render() {
    val (c1, c2, c3) = List(3) { if (step <= 2*it ) "yellow" else "blue" }
    val (s1, s2, s3) = List(3) { if (step == 2*it) Spinner.get() else ""}
    ansi().eraseScreen().render("""

      Ping  $s1 @|$c1 $ping ms|@
  Download  $s2 @|$c2 $download|@
    Upload  $s3 @|$c3 $upload|@

""").println()
}

object Spinner {
    fun get(): String {
        val current = System.currentTimeMillis().div(80).rem(spinners.size).toInt()
        val next = spinners[current]
        return next
    }
    val simpleSpinners = listOf("/", "-", "\\", "-")
    val spinners = simpleSpinners
}


data class SpeedtestResult(val ping: Int, val download: String, val upload: String, val step: Int)
private fun Ansi.println() = println(this)
