package iv.nakonechnyi.newsfeeder.net

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import iv.nakonechnyi.newsfeeder.model.Article

class NewsLoader(context: Context, private val url: String) : AsyncTaskLoader<List<Article>>(context) {
    override fun onStartLoading() = forceLoad()
    override fun loadInBackground(): List<Article>? = NewsLoaderHelper(url).fetchNews()
}