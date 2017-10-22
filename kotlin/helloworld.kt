#!/usr/bin/env kotlin-script.sh
package scripts.helloworld

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.*
import org.fusesource.jansi.Ansi.Color.*
import org.fusesource.jansi.AnsiConsole

fun main(args: Array<String>) {
    println(ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset())

}