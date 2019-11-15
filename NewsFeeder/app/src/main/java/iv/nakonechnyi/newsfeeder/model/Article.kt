package iv.nakonechnyi.newsfeeder.model

import android.graphics.Bitmap

class Article(
    val title: String,
    val url: String,
    val bitmap: Bitmap?,
    val dateOfPublishing: Long
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Article

        if (title != other.title) return false
        if (url != other.url) return false
        if (bitmap != other.bitmap) return false
        if (dateOfPublishing != other.dateOfPublishing) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (bitmap?.hashCode() ?: 0)
        result = 31 * result + dateOfPublishing.hashCode()
        return result
    }
}

object UrlHolder {
    var url: String? = null
}