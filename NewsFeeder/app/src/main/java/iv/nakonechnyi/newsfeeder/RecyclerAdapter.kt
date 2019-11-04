package iv.nakonechnyi.newsfeeder

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.newsfeeder.model.Article
import kotlinx.android.synthetic.main.one_list_item_news.view.*

class RecyclerAdapter(owner: LifecycleOwner, liveData: LiveData<List<Article>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = listOf<Article>()
    lateinit var onClickListener: OnItemClickListener

    init {
        liveData.observe(owner, Observer {
            data = it
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.one_list_item_news, parent, false)
        return NewsHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        with(holder as NewsHolder) {
            itemView.setOnClickListener {
                onClickListener.onItemClick(data[position].url)
            }
            bind(data[position])
        }
    }

    override fun getItemCount(): Int = data.size

    private inner class NewsHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(article: Article) {
            itemView.title.text = article.title
            itemView.article_image.setImageURI(Uri.parse(article.urlToImage))
        }
    }

}