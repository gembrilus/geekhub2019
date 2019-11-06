package iv.nakonechnyi.newsfeeder.net

import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.stringToLongDate
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset


class NewsLoaderHelper(
    private val stringUrl: String
) {

    suspend fun fetchNews(): List<Article> {
        val url = createUrl(stringUrl)
        val handler = CoroutineExceptionHandler {_, exception ->
            exception.printStackTrace()
        }
        val jsonResponse = GlobalScope.async(Dispatchers.IO + handler) { makeHTTPRequest(url) }
        val articles = GlobalScope.async(Dispatchers.IO + handler) { extractArticlesFromJson(jsonResponse.await()) }
        return articles.await()
    }

    private fun extractArticlesFromJson(jsonResponse: String?): List<Article> {
        if (jsonResponse == null) return emptyList()

        val list = mutableListOf<Article>()

        try {
            val jsonObject = JSONObject(jsonResponse)
            if (jsonObject.getString("status") == "ok") {
                val jsonArray = jsonObject.getJSONArray("articles")

                var i = 0
                while (i < jsonArray.length()) {
                    val articleJson = jsonArray.getJSONObject(i++)
                    val article = with(articleJson) {
                        Article(
                            getString("title"),
                            getString("url"),
                            getString("urlToImage"),
                            stringToLongDate(getString("publishedAt")) ?: 0L
                        )
                    }
                    list += article
                }
            }
            return list
        } catch (e: JSONException){
            e.printStackTrace()
        }

        return emptyList()
    }

    @Throws(IOException::class)
    private fun makeHTTPRequest(url: URL?): String {
        var jsonResponse = ""
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url?.let {
                (it.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    readTimeout = 10000
                    connectTimeout = 15000
                    connect()
                }
            }
            inputStream = urlConnection?.inputStream
            jsonResponse = readFromStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
                urlConnection?.disconnect()
                inputStream?.close()
        }
        return jsonResponse
    }

    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }


    private fun createUrl(stringUrl: String): URL? {
        var url: URL? = null
        try {
            url = URL(stringUrl)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return url
    }
}