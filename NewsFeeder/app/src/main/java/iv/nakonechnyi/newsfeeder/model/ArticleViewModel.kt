package iv.nakonechnyi.newsfeeder.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object ArticleViewModel : ViewModel() {

    val data = MutableLiveData<MutableList<Article>>()

    init {
        data.value = mutableListOf()
    }

    fun getArticles() : MutableList<Article> = data.value ?: mutableListOf()

    fun setArticles(list: MutableList<Article>) {
        data.value = list
    }

    fun getArticle(pos: Int) = data.value?.get(pos)

    fun getArticle(url: String?) = data.value?.find { it.url == url }

    fun addArticle(article: Article) = data.value?.add(article)

    fun removeArticle(article: Article) = data.value?.remove(article)

    override fun onCleared() {
        super.onCleared()
        data.value = null
    }

}