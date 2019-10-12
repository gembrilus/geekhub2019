package com.example.aboutme

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val data: Array<String>) :  RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.MyViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_items, parent, false) as ImageView
        return MyViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.imageView.apply {
            adjustViewBounds = true
            setImageURI(Uri.parse(data[position]))
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    override fun getItemCount() = data.size
}
