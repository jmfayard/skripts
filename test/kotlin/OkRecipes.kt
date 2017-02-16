import io.kotlintest.specs.StringSpec
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class OkRecipes : StringSpec() { init {


    "Synchronous get" {
        val client = OkHttpClient()

        val request = Request.Builder().url("http://publicobject.com/helloworld.txt").build()

        val response = client.newCall(request).execute()
        require(response.isSuccessful) { "Unexpected code $response" }

        val responseHeaders = response.headers()
        for (i in 0..responseHeaders.size() - 1) {
            println(responseHeaders.name(i) + ": " + responseHeaders.value(i))
        }

        System.out.println(response.body().string())
    }

    "Post Markdown" {
        val MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8")

        val client = OkHttpClient()

        val postBody = """
            Releases
            --------

            * _1.0_ May 6, 2013
            * _1.1_ June 15, 2013
            * _1.2_ August 11, 2013\n
            """.trimIndent()

        val request = Request.Builder().url("https://api.github.com/markdown/raw")
            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
            .build()

        val response = client.newCall(request).execute()
        require (response.isSuccessful) { "Unexpected code $response " }

        System.out.println(response.body().string())
    }
    }
}



