package com.javadEsl.imageSearchApp.ui.details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.imageSearchApp.R
import com.javadEsl.imageSearchApp.data.UnsplashPhoto
import com.javadEsl.imageSearchApp.data.convertedUrl
import com.javadEsl.imageSearchApp.databinding.ItemUnsplashPhotoBinding
import com.javadEsl.imageSearchApp.databinding.ItemUnsplashUserPhotoBinding
import com.javadEsl.imageSearchApp.databinding.UnsplashPhotoLoadStateFooterBinding
import com.javadEsl.imageSearchApp.ui.gallery.UnsplashPhotoAdapter

class TodoAdapter(
    var todos: List<UnsplashPhoto>,
    private val listener: TodoAdapter.OnItemClickListener
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

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
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_error)
                    .into(imageView)


                textViewItemPhotoLikes.text = photo.likes.toString()
                cardViewColor.setBackgroundColor(Color.parseColor(photo.color))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: UnsplashPhoto)
    }
}


