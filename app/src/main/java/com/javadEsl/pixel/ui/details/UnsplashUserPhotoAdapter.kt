package com.javadEsl.pixel.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.model.search.PixelPhoto
import com.javadEsl.pixel.data.model.search.convertedUrl
import com.javadEsl.pixel.databinding.ItemUnsplashUserPhotoBinding

class UnsplashUserPhotoAdapter(
    var todos: List<PixelPhoto>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UnsplashUserPhotoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            ItemUnsplashUserPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    inner class TodoViewHolder(private val binding: ItemUnsplashUserPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                todos[bindingAdapterPosition].let { listener.onItemClick(it) }
            }
        }

        fun bind() {
            val photo = todos[bindingAdapterPosition]
            binding.apply {

                Glide.with(itemView)
                    .load(photo.urls?.regular?.convertedUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error_photos)
                    .into(imageView)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: PixelPhoto)
    }
}


