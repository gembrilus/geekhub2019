package iv.nakonechnyi.newsfeeder.net

import android.content.Context
import android.util.Log
import iv.nakonechnyi.newsfeeder.displayErrorMessage
import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.stringToLongDate
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
    private val context: Context,
    private val stringUrl: String
) {

    fun fetchNews(): List<Article>? {
        val url = createUrl(stringUrl)

        var jsonResponse = ""

        try {
            jsonResponse = makeHTTPRequest(url)
        } catch (e: IOException) {
            e.printStackTrace()
//            displayErrorMessage(context, "Failed to close the connection.")
        }
        return extractArticlesFromJson(jsonResponse)
    }

    private fun extractArticlesFromJson(jsonResponse: String): List<Article> {

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
            Log.d("ARTICLES", list.toString())
            return list
        } catch (e: JSONException){
            e.printStackTrace()
//            displayErrorMessage(context, "Error of reading a response from server")
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
//            displayErrorMessage(context, "Connection or reading from URL is failed.")
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
//            displayErrorMessage(context, "Url has incorrect format.")
        }
        return url
    }

}