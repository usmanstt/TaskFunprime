package com.example.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions




class ImagesAdapter(var imageList: ArrayList<Int>, var imageInterface: ImageInterface) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image: ImageView = itemView.findViewById(R.id.images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.images,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = imageList[position]
        val myOptions = RequestOptions()
            .override(200, 200)
        Glide.with(holder.image.context).asBitmap().apply(myOptions).load(current).into(holder.image)
        holder.image.setOnClickListener {
            imageInterface.DownloadImage(current, position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}