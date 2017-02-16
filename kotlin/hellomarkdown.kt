#!/usr/bin/env kotlin-script.sh
package hellomarkdown

import debug
import okSink
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import osxOpenFile
import java.io.File

fun main(args: Array<String>) {
    postMarkdown(File("README.md"), File("README.html"))

    osxOpenFile(File("README.html"))
}

fun postMarkdown(file: File, dest: File) {
    require(file.canRead()) { "Cannot find ${file.absolutePath}" }

    val MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8")

    val client = OkHttpClient()

    val request = Request.Builder().url("https://api.github.com/markdown/raw")
        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
        .build()

    val response = client.newCall(request).execute()
    require (response.isSuccessful) { "Unexpected code $response " }

    val sink = dest.okSink()
    sink.writeAll(response.body().source())
    sink.close()

}