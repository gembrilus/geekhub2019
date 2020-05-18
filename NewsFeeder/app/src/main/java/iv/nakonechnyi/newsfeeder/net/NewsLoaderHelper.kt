package iv.nakonechnyi.newsfeeder.net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.util.stringToLongDate
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import java.nio.charset.Charset


class NewsLoaderHelper(
    private val stringUrl: String, private val exceptionHandler: NetworkExceptionHandler? = null
) {

    suspend fun fetchNews(): List<Article> {
        val url = createUrl(stringUrl)
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }
        val jsonResponse = GlobalScope.async(Dispatchers.IO + handler) {
            makeHTTPRequest(url) { stream ->
                readFromStream(stream)
            }
        }
        val articles =
            GlobalScope.async(Dispatchers.IO + handler) { extractArticlesFromJson(jsonResponse.await()) }
        return articles.await()
    }

    private suspend fun fetchImage(sUrl: String): Bitmap? {
        val url = createUrl(sUrl)
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }
        val jsonResponse = GlobalScope.async(Dispatchers.IO + handler) {
            makeHTTPRequest(url) { stream ->
                readImageFromStream(stream)
            }
        }
        return jsonResponse.await()
    }

    private fun extractArticlesFromJson(jsonResponse: String?): List<Article> {
        if (jsonResponse == null || jsonResponse.isEmpty()) return emptyList()

        val list = mutableListOf<Article>()

        try {
            val jsonObject = JSONObject(jsonResponse)
            if (jsonObject.getString("status") == "ok") {
                val jsonArray = jsonObject.getJSONArray("articles")

                var i = 0
                while (i < jsonArray.length()) {
                    val articleJson = jsonArray.getJSONObject(i++)
                    val urlImage = articleJson.getString("urlToImage")
                    var bitmap: Bitmap? = null
                    runBlocking(Dispatchers.IO) {
                        bitmap = fetchImage(urlImage)
                    }
                    val article = with(articleJson) {
                        Article(
                            getString("title"),
                            getString("url"),
                            bitmap,
                            stringToLongDate(getString("publishedAt"))
                                ?: 0L
                        )
                    }
                    list += article
                }
            }
            return list
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return emptyList()
    }

    @Throws(IOException::class)
    private fun <T> makeHTTPRequest(url: URL?, loader: (InputStream) -> T): T? {
        var response: T? = null
        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url?.let {
                (it.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    readTimeout = 10000
                    connectTimeout = 15000
                }
            }
            if (urlConnection != null) {
                urlConnection.connect()
                inputStream = urlConnection.inputStream
                try {
                    response = loader(inputStream)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        } catch (e: UnknownHostException) {
            exceptionHandler?.onNoNetworkConnection()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
            return response
        }
    }

    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream): String? {
        BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            .use { reader ->
                val output = StringBuilder()
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
                return output.toString()
            }
    }

    @Throws(IllegalArgumentException::class)
    private fun readImageFromStream(inputStream: InputStream): Bitmap? {
        var bitmap = BitmapFactory.decodeStream(inputStream)
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        if (bitmapHeight > 128 || bitmapWidth > 128) {
            val scale = minOf(bitmapHeight / 128, bitmapWidth / 128)
            bitmap =
                Bitmap.createScaledBitmap(bitmap, bitmapWidth / scale, bitmapHeight / scale, false)
        }
        return bitmap
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

    interface NetworkExceptionHandler {
        fun onNoNetworkConnection()
    }
}