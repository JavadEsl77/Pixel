package com.javadEsl.pixel.ui.gallery

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.convertedUrl
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment :
    Fragment(R.layout.fragment_gallery),
    UnsplashPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getRandomPhoto()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter(this)
        binding.apply {

            viewModel.liveDataRandomPhoto.observe(viewLifecycleOwner) {
                if (it != null) {
                    Glide.with(requireActivity())
                        .load(it.urls?.regular?.convertedUrl)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.img)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                p0: GlideException?,
                                p1: Any?,
                                p2: Target<Drawable>?,
                                p3: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                p0: Drawable?,
                                p1: Any?,
                                p2: Target<Drawable>?,
                                p3: DataSource?,
                                p4: Boolean
                            ): Boolean {
                                return false
                            }

                        })
                        .into(imageViewBanner)
                }
            }

            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() },
            )

            buttonRetry.setOnClickListener {
                adapter.retry()
            }

            imgClean.setOnClickListener {
                edtSearch.setText("")
                imgClean.isVisible = false
            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    imgClean.isVisible = edtSearch.text != null
                }
            })

            edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val inputMethodManager =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    performSearch()
                    return@OnEditorActionListener true
                }
                false
            })

        }
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
    }

    private fun performSearch() {
        if (binding.edtSearch.text.toString().isEmpty()) {
            binding.edtSearch.error = getString(R.string.string_error_edittext_search)
        } else {
            viewModel.searchPhotos(binding.edtSearch.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        if (checkConnection()) {
            val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
            findNavController().navigate(action)
        } else {
            val view: View = requireView()
            Snackbar.make(
                view, "check your internet 😐",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun checkConnection(): Boolean {
        val connectionManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (return wifiConnection?.isConnectedOrConnecting == true || (mobileDataConnection?.isConnectedOrConnecting == true)) true


    }


}