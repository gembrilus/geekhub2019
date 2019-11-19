package iv.nakonechnyi.specialnumbers.pager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import iv.nakonechnyi.specialnumbers.R
import kotlinx.android.synthetic.main.number_item.view.*

class PrimeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var list = mutableListOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.number_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val number = list[position]
        (holder as Holder).bind(number)
    }

    fun addItem(num: Long) {
        list.add(num)
        notifyItemChanged(list.indexOf(list.last()))
    }

    fun clear(){
        list = mutableListOf()
        notifyDataSetChanged()
    }


    private inner class Holder(view: View) : RecyclerView.ViewHolder(view){

        fun bind(n: Long){
            itemView.number.text = n.toString()
        }

    }

}