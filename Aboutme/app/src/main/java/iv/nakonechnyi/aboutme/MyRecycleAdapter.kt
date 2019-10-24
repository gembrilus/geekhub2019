package iv.nakonechnyi.aboutme

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MyRecycleAdapter(private val mPhotos: List<String>):
    RecyclerView.Adapter<MyRecycleAdapter.ImageHolder>() {

    lateinit var clickListener: ClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_item_image, parent, false) as ImageView
        return ImageHolder(imageView)
    }
    override fun getItemCount() = mPhotos.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        (holder.itemView as ImageView).setImageURI(Uri.parse(mPhotos[position]))
    }

    inner class ImageHolder(imageView: ImageView):
        RecyclerView.ViewHolder(imageView), View.OnClickListener {

        init { itemView.setOnClickListener(this) }
        override fun onClick(view: View?) {
            if(adapterPosition != 0) return
            view?.let { clickListener.onItemClick(adapterPosition, it) }
        }
    }
}