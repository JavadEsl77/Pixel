package com.javadEsl.pixel.ui.myDownload

import android.graphics.Color.alpha
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.ItemListMyDownloadBinding
import com.javadEsl.pixel.size
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
        holder.bind(downloadList[position])

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

        fun bind(photo: File) {

            binding.apply {

                val quality = photo.name.replace(".jpg", "")
                when (quality) {
                    "Full-HD" -> {
                        cardFullHd.isVisible = true
                    }
                }
                textViewQuality.text = quality
                textViewFileSize.text = "( " + photo.size() + " )"

                Glide.with(itemView)
                    .load(photo)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
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


