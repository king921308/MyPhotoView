package com.example.myphotoview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myphotoview.R
import com.example.myphotoview.bean.PhotoItem
import kotlinx.android.synthetic.main.cell_photo.view.*

class PagerPhotoAdapter: ListAdapter<PhotoItem, CellViewHolder>(DIFFCALLBANCK) {

    object DIFFCALLBANCK :DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId==newItem.photoId
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.cell_photo,parent,false).apply {
            return CellViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        holder.itemView.textViewTags.text="关键词："+getItem(position).photoTags
        Glide.with(holder.itemView)
            .load(getItem(position).fullUrl)
            .placeholder(R.drawable.photo_placehoder)
            .into(holder.itemView.imageViewBig)
    }
}
class PhotoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)