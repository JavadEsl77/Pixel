package com.javadEsl.pixel.ui.myDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.ItemListMyDownloadBinding
import java.io.File

class MyDownloadAdapter(
    var downloadList: List<File>,
    private val listener: MyDownloadFragment
) : RecyclerView.Adapter<MyDownloadAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            ItemListMyDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return downloadList.size
    }

    inner class TodoViewHolder(private val binding: ItemListMyDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                downloadList[bindingAdapterPosition].let { listener.onItemClick(it) }
            }
        }

        fun bind() {
            val photo = downloadList[bindingAdapterPosition]
            binding.apply {
                Glide.with(itemView)
                    .load(photo)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error_photos)
                    .into(imageView)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: File)
    }
}


