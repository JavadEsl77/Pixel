package com.javadEsl.pixel.ui.myDownload

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.ItemListMyDownloadBinding
import com.javadEsl.pixel.helper.extensions.size
import java.io.File

class MyDownloadAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<File, MyDownloadAdapter.TodoViewHolder>(Comparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            ItemListMyDownloadBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TodoViewHolder(private val binding: ItemListMyDownloadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition).let {
                    listener.onItemClick(
                        it,
                        bindingAdapterPosition
                    )
                }
            }
        }

        fun bind(photo: File) {

            binding.apply {
                textViewFileSize.text = "( " + photo.size() + " )"

                Glide.with(itemView)
                    .load(photo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_error_photos)
                    .into(imageView)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: File, position: Int)
    }

    companion object {
        private val Comparator = object : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(oldItem: File, newItem: File) =
                oldItem.name.hashCode() == newItem.name.hashCode()

            override fun areContentsTheSame(oldItem: File, newItem: File) = oldItem == newItem
        }
    }
}


