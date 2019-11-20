package iv.nakonechnyi.specialnumbers.pager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.specialnumbers.Model
import iv.nakonechnyi.specialnumbers.R
import kotlinx.android.synthetic.main.number_item.view.*

class PrimeAdapter(private val model: Model) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.number_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount() = model.data.value?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val number = model.data.value?.get(position) ?: 0
        (holder as Holder).bind(number)
    }

    private inner class Holder(view: View) : RecyclerView.ViewHolder(view){

        fun bind(n: Long){
            itemView.number.text = n.toString()
        }

    }

}