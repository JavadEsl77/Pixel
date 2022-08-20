package com.javadEsl.imageSearchApp.ui.details

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.javadEsl.imageSearchApp.R
import com.javadEsl.imageSearchApp.api.UnsplashApi
import com.javadEsl.imageSearchApp.data.ModelPhoto
import com.javadEsl.imageSearchApp.data.UnsplashRepository
import com.javadEsl.imageSearchApp.data.convertedUrl
import com.javadEsl.imageSearchApp.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject


@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {


    @Inject
    lateinit var photo: UnsplashRepository
    private val viewModel by viewModels<DetailViewModel>()
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPhotoDetail(args.photo.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDetailsBinding.bind(view)

        binding.apply {
            val photo = args.photo

//            textViewDescription.text = photo.description
            val uri = Uri.parse(photo.user?.attributionUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            textViewCreator.apply {
                text = "photo by ${photo.user?.name} on Unsplash"
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }

            initViewModel(binding)


        }
    }

    private fun initViewModel(binding: FragmentDetailsBinding) {

        viewModel.liveDataList.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            setDetail(binding,it)
        }
    }

    private fun setDetail(binding: FragmentDetailsBinding,modelPhoto: ModelPhoto) {
        binding.apply {
            textViewUserName.text = modelPhoto.user?.name
            Glide.with(this@DetailsFragment)
                .load(modelPhoto.user?.profileImage?.large?.convertedUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_user)
                .into(imageViewProfile)

            textViewTitleCreatedAt.isVisible = modelPhoto.createdAt != null
            textViewCreatedAt.isVisible = modelPhoto.createdAt != null
            textViewCreatedAt.text = modelPhoto.createdAt
            textViewLikes.text = modelPhoto.likes.toString()
            cardViewColor.setBackgroundColor(Color.parseColor(modelPhoto.color))

            Glide.with(this@DetailsFragment)
                .load(modelPhoto.urls?.full?.convertedUrl)
                .error(R.drawable.ic_baseline_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        textViewCreator.isVisible = true
//                        textViewDescription.isVisible = modelPhoto.description != null
                        return false
                    }
                })
                .into(imageView)
        }

    }


}