package iv.nakonechnyi.newsfeeder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.newsfeeder.model.Article
import iv.nakonechnyi.newsfeeder.net.NewsLoaderHelper
import kotlinx.android.synthetic.main.one_list_item_news.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecyclerAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listDiffer: AsyncListDiffer<Article>  = AsyncListDiffer(this, DIFF_CALLBACK)
    lateinit var onClickListener: OnItemClickListener

    var position: Int = 0

    fun submit(list: List<Article>){
        listDiffer.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_list_item_news, parent, false)
        return NewsHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {

        with(holder as NewsHolder) {

            val currentArticle = listDiffer.currentList[pos]

            itemView.setOnClickListener {
                this@RecyclerAdapter.position = pos
                onClickListener.onItemClick(currentArticle.url)
            }
            bind(currentArticle)
        }
    }

    override fun getItemCount(): Int = listDiffer.currentList.size

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {

            override fun areItemsTheSame(oldArticle: Article, newArticle: Article): Boolean {
                return oldArticle.title == newArticle.title
            }
            override fun areContentsTheSame(oldArticle: Article, newArticle: Article): Boolean {
                return oldArticle == newArticle
            }
        }
    }


    private inner class NewsHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(article: Article) {
            itemView.title.text = article.title
            GlobalScope.launch(Dispatchers.IO) {
                val image = NewsLoaderHelper(article.urlToImage).fetchImage()
                launch(Dispatchers.Main) {
                    itemView.article_image.setImageBitmap(image)
                }
            }
        }
    }

}