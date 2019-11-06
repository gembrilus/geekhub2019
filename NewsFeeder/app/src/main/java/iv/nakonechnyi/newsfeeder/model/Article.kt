package iv.nakonechnyi.newsfeeder.model

class Article(
    val title: String,
    val url: String,
    val urlToImage: String,
    val dateOfPublishing: Long
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (title != other.title) return false
        if (url != other.url) return false
        if (urlToImage != other.urlToImage) return false
        if (dateOfPublishing != other.dateOfPublishing) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + urlToImage.hashCode()
        result = 31 * result + dateOfPublishing.hashCode()
        return result
    }
}