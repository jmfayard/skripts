#!/usr/bin/env kotlin-script.sh
package hellomarkdown

import jmfayard.osxOpenFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.buffer
import okio.sink
import java.io.File

fun main(args: Array<String>) {
    postMarkdown(File("README.md"), File("README.html"))

    osxOpenFile(File("README.html"))
}

fun postMarkdown(file: File, dest: File) {
    require(file.canRead()) { "Cannot find ${file.absolutePath}" }

    val MEDIA_TYPE_MARKDOWN = "text/x-markdown; charset=utf-8".toMediaTypeOrNull()

    val client = OkHttpClient()

    val request = Request.Builder().url("https://api.github.com/markdown/raw")
        .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
        .build()

    val response = client.newCall(request).execute()
    val body = requireNotNull(response.body) { "Unexpected code $response " }

    dest.sink().buffer().use { sink ->
        sink.writeAll(body.source())
    }
}
