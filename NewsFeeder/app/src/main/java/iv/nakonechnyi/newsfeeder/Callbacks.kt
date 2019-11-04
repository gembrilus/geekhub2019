package iv.nakonechnyi.newsfeeder

interface Callbacks {
    fun onArticleSelected(url: String)
}

interface OnItemClickListener{
    fun onItemClick(url: String)
}