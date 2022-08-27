package com.javadEsl.pixel.ui.gallery

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.convertedUrl
import com.javadEsl.pixel.databinding.ItemUnsplashPhotoBinding
import com.javadEsl.pixel.isBrightColor


class UnsplashPhotoAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { listener.onItemClick(it) }
            }

        }

        fun bind(photo: UnsplashPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urls?.regular?.convertedUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error_photos)
                    .into(imageView)


                Glide.with(itemView)
                    .load(photo.user?.profile_image?.medium?.convertedUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_user)
                    .into(imageViewProfile)

                textViewUserName.text = photo.user?.name
                textViewLikes.text = photo.likes.toString()
                viewBackItem.setBackgroundColor(
                    Color.parseColor(
                        "#cc" + photo.color?.replace(
                            "#",
                            ""
                        )
                    )
                )

                if (Color.parseColor(photo.color.toString()).isBrightColor) {
                    textViewUserName.setTextColor(Color.parseColor("#000000"))
                    textViewLikes.setTextColor(Color.parseColor("#000000"))
                    textViewLikes.compoundDrawables[0].setTint(Color.parseColor("#000000"))
                } else {
                    textViewUserName.setTextColor(Color.parseColor("#ffffff"))
                    textViewLikes.setTextColor(Color.parseColor("#B3FFFFFF"))
                    textViewLikes.compoundDrawables[0].setTint(Color.parseColor("#ffffff"))

                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: UnsplashPhoto)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem
        }
    }


}


