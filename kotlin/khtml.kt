#!/usr/bin/env kotlin-script.sh
package khtml

import kotlinx.html.*
import kotlinx.html.dom.*
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


