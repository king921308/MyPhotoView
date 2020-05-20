package com.example.myphotoview.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myphotoview.viewmodel.GalleryViewModel
import com.example.myphotoview.R
import com.example.myphotoview.bean.PhotoItem
import com.example.myphotoview.data.NetWorkStatus
import kotlinx.android.synthetic.main.cell_footer.view.*
import kotlinx.android.synthetic.main.cell_gallery.view.*

class GalleryAdapter(private val galleryViewModel: GalleryViewModel) :
    PagedListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFFCALLBACK) {
    private var netWorkStatus: NetWorkStatus? = null
    private var hasFooter = false
    init {
        galleryViewModel.retry()
    }
    fun updateNetWorkStatus(netWorkStatus: NetWorkStatus) {
        this.netWorkStatus = netWorkStatus
        if (netWorkStatus == NetWorkStatus.INITIAL_LOADING)
            hideFooter()
        else
            showFooter()
    }

    private fun hideFooter() {
        if (hasFooter) {
            notifyItemRemoved(itemCount - 1)
        }
        hasFooter = false
    }

    private fun showFooter() {
        if (hasFooter) {
            notifyItemChanged(itemCount - 1)
        } else {
            hasFooter = true
            notifyItemInserted(itemCount - 1)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasFooter) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasFooter && position == itemCount - 1) R.layout.cell_footer else R.layout.cell_gallery
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.cell_gallery -> CellViewHolder.newViewHolder(
                parent
            ).also { holder ->
                holder.itemView.setOnClickListener {
                    Bundle().apply {
                        putParcelableArrayList("PHOTO", ArrayList(currentList!!))
                        putInt("PHOTO_POSITION", holder.absoluteAdapterPosition)
                        holder.itemView.findNavController()
                            .navigate(R.id.action_galleryFragment_to_pagerPhotoFragment, this)
                    }
                }
            }
            else -> FootViewHolder.newViewHolder(
                parent
            ).also {
                it.itemView.setOnClickListener {
                    galleryViewModel.retry()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.cell_footer -> (holder as FootViewHolder).bindWithNetWorkStatus(netWorkStatus!!)
            else -> {
                val photoItem = getItem(position) ?: return
                (holder as CellViewHolder).bindWithPhotoItem(photoItem)
            }
        }

    }
}

class CellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newViewHolder(parent: ViewGroup): CellViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cell_gallery, parent, false)
            return CellViewHolder(view)
        }
    }

    fun bindWithPhotoItem(photoItem: PhotoItem) {
        with(itemView) {
            shimmerView.apply {
                setShimmerColor(0x55FFFFFF)
                setShimmerAngle(0)
                startShimmerAnimation()
            }
            textViewUser.text = photoItem.photoUser
            textViewFav.text = photoItem.photoFav.toString()
            textViewLike.text = photoItem.photoLikes.toString()
            imageView.layoutParams.height = photoItem.photoHeight
        }
        Glide.with(itemView)
            .load(photoItem.previewUrl)
            .placeholder(R.drawable.photo_placehoder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false.also { itemView.shimmerView?.stopShimmerAnimation() }
                }

            })
            .into(itemView.imageView)

    }
}

class FootViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newViewHolder(parent: ViewGroup): FootViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cell_footer, parent, false)
            (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            return FootViewHolder(view)
        }
    }

    fun bindWithNetWorkStatus(netWorkStatus: NetWorkStatus) {
        with(itemView) {
            when (netWorkStatus) {
                NetWorkStatus.FAILED -> {
                    loadTextView.text = "加载失败，点击重试"
                    loadBar.visibility = View.GONE
                    isClickable = true
                }
                NetWorkStatus.COMPLETED -> {
                    loadTextView.text = "加载完毕"
                    loadBar.visibility = View.GONE
                    isClickable = false
                }
                else -> {
                    loadTextView.text = "正在加载"
                    loadBar.visibility = View.VISIBLE
                    isClickable = false
                }
            }
        }
    }
}
