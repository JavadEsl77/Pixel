package com.javadEsl.pixel.ui.gallery

import android.app.Activity
import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.BuildConfig
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.convertedUrl
import com.javadEsl.pixel.databinding.ItemUnsplashAdsBinding
import com.javadEsl.pixel.databinding.ItemUnsplashPhotoBinding
import com.javadEsl.pixel.isBrightColor
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel


class UnsplashPhotoAdapter(
    private val listener: OnItemClickListener,
    private val activity: Activity
) :
    PagingDataAdapter<UnsplashPhoto, RecyclerView.ViewHolder>(PHOTO_COMPARATOR) {

    private var lastPosition = 0

    override fun getItemViewType(position: Int): Int {
        return if (snapshot().items[position].isAdvertisement) {
            TYPE_AD
        } else {
            TYPE_PHOTO
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_AD -> AdViewHolder(ItemUnsplashAdsBinding.inflate(inflater, parent, false))
            else    -> PhotoViewHolder(ItemUnsplashPhotoBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhotoViewHolder -> getItem(position)?.let { holder.bind(it) }
            is AdViewHolder    -> getItem(position)?.let { holder.bind(it) }
        }
    }

    inner class AdViewHolder(private val binding: ItemUnsplashAdsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { listener.onItemClick(it) }
            }
        }

        fun bind(photo: UnsplashPhoto) {
            var responseId = ""
            binding.apply {

                if (bindingAdapterPosition == itemCount - 1) {
                    val param = tapsellNativeadCtaView.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(25, 25, 0, 25)
                    tapsellNativeadCtaView.layoutParams = param
                }
                setAnimation(tapsellNativeadCtaView, bindingAdapterPosition)

                var adHolder: AdHolder = TapsellPlus.createAdHolder(
                    activity,
                    binding.adContainerGallery, R.layout.item_unsplash_ads
                )!!

                TapsellPlus.requestNativeAd(
                    activity,
                    BuildConfig.TAPSELL_NATIVE_BANNER,
                    object : AdRequestCallback() {
                        override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                            super.response(tapsellPlusAdModel)
                            responseId = tapsellPlusAdModel.responseId
                            showAd(adHolder,responseId)
                        }

                        override fun error(message: String) {
                            Log.e(ContentValues.TAG, "error: $message")
                        }
                    })

            }
        }
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

                if (bindingAdapterPosition == itemCount - 1) {
                    val param = cardGalleryItem.layoutParams as ViewGroup.MarginLayoutParams
                    param.setMargins(25, 25, 0, 25)
                    cardGalleryItem.layoutParams = param
                }

                setAnimation(cardGalleryItem, bindingAdapterPosition)

                Glide.with(itemView)
                    .load(photo.urls?.regular?.convertedUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
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
        private const val TYPE_PHOTO = 1
        private const val TYPE_AD = 2

        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            viewToAnimate.animation =
                AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.translate)

            lastPosition = position
        }
    }

    private fun showAd(adHolder: AdHolder, responseId: String) {
        TapsellPlus.showNativeAd(activity, responseId, adHolder,
            object : AdShowListener() {
                override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                    super.onOpened(tapsellPlusAdModel)
                    Log.d(ContentValues.TAG, "Ad Open")
                }

                override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                    super.onError(tapsellPlusErrorModel)
                    Log.e("onError", tapsellPlusErrorModel.toString())
                }
            })
    }
}


