package com.javadEsl.pixel.ui.details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.convertedUrl
import com.javadEsl.pixel.databinding.ItemUnsplashUserPhotoBinding
import com.javadEsl.pixel.isBrightColor

class UnsplashUserPhotoAdapter(
    var todos: List<UnsplashPhoto>,
    private val listener: UnsplashUserPhotoAdapter.OnItemClickListener
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
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error_photos)
                    .into(imageView)

                if (photo.likes.toString().isNotEmpty()) {
                    textViewItemTitlePhotoLikes.isVisible = true
                    textViewItemPhotoLikes.isVisible = true
                    textViewItemPhotoLikes.text = photo.likes.toString()
                }

                if (photo.user?.name.toString().isNotEmpty()) {
                    textViewItemTitlePhotoDownload.isVisible = true
                    textViewItemPhotoDownload.isVisible = true
                    textViewItemPhotoDownload.text = photo.user?.name.toString()
                }

                layoutItemUserPhoto.setBackgroundColor(
                    Color.parseColor(
                        "#cc" + photo.color?.replace("#", "")
                    )
                )
                if (Color.parseColor(photo.color.toString()).isBrightColor) {
                    textViewItemPhotoLikes.setTextColor(Color.parseColor("#000000"))
                    textViewItemTitlePhotoLikes.setTextColor(Color.parseColor("#000000"))
                    textViewItemTitlePhotoDownload.setTextColor(Color.parseColor("#000000"))
                    textViewItemPhotoDownload.setTextColor(Color.parseColor("#000000"))
                } else {
                    textViewItemPhotoLikes.setTextColor(Color.parseColor("#ffffff"))
                    textViewItemTitlePhotoLikes.setTextColor(Color.parseColor("#B3FFFFFF"))
                    textViewItemTitlePhotoDownload.setTextColor(Color.parseColor("#B3FFFFFF"))
                    textViewItemPhotoDownload.setTextColor(Color.parseColor("#ffffff"))
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: UnsplashPhoto)
    }
}


