package iv.nakonechnyi.newsfeeder

interface Callbacks {
    fun onArticleSelected(url: String, position: Int)
}