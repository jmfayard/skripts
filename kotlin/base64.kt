#!/usr/bin/env kotlin-script.sh
package base64

import okio.Buffer
import okio.Okio
import java.io.File

fun main(args: Array<String>) {
    val file = File(args[0])
//    val file = File("/Users/jmfayard/Downloads/sourcetrail_license.txt")
    println(base64(file))
}


fun base64(file: File): String {
    check(file.canRead())
    Buffer().use { buffer ->
        Okio.source(file).use { source ->
            buffer.writeAll(source)
        }
        return buffer.readByteString().base64()
    }

}