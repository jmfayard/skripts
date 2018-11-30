#!/usr/bin/env kotlin-script.sh
package khtml

import kotlinx.html.ATarget
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

fun main(args: Array<String>) {

    System.out.appendHTML().html {
        body {
            div {
                a("http://kotlinlang.org") {
                    target = ATarget.blank
                    +"Main <a> site"
                }
            }
        }
    }
}
