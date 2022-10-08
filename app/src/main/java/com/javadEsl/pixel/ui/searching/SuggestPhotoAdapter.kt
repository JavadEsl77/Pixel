package com.javadEsl.pixel.ui.searching

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.data.search.convertedUrl
import com.javadEsl.pixel.databinding.ItemSuggestPhotoBinding
import com.javadEsl.pixel.isBrightColor


class SuggestPhotoAdapter(
    var suggestPhotoList: List<PixelPhoto>,
    private val listener: OnItemClickListener,
    private val activity: Activity
) : RecyclerView.Adapter<SuggestPhotoAdapter.SuggestViewHolder>() {
    private var lastPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestViewHolder {
        val binding =
            ItemSuggestPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SuggestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return suggestPhotoList.size
    }

    inner class SuggestViewHolder(private val binding: ItemSuggestPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                suggestPhotoList[bindingAdapterPosition].let { listener.onItemClick(it) }
            }
        }

        fun bind() {
            binding.apply {

                 //setAnimation(cardSuggestItem, bindingAdapterPosition)

                Glide.with(itemView)
                    .load(suggestPhotoList[bindingAdapterPosition].urls?.regular?.convertedUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error_photos)
                    .into(imageView)


                Glide.with(itemView)
                    .load(suggestPhotoList[bindingAdapterPosition].user?.profile_image?.medium?.convertedUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_user)
                    .into(imageViewProfile)

                textViewUserName.text = suggestPhotoList[bindingAdapterPosition].user?.name
                viewBackItem.setBackgroundColor(
                    Color.parseColor(
                        "#cc" + suggestPhotoList[bindingAdapterPosition].color?.replace(
                            "#",
                            ""
                        )
                    )
                )

                if (Color.parseColor(suggestPhotoList[bindingAdapterPosition].color.toString()).isBrightColor) {
                    textViewUserName.setTextColor(Color.parseColor("#000000"))
                } else {
                    textViewUserName.setTextColor(Color.parseColor("#ffffff"))
                }

            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: PixelPhoto)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            viewToAnimate.animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.from_left)

            lastPosition = position
        }
    }

}


