package com.javadEsl.pixel.ui.gallery

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
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
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.convertedUrl
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import io.sentry.Sentry

@AndroidEntryPoint
class GalleryFragment :
    Fragment(R.layout.fragment_gallery),
    UnsplashPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private var sharedPreference: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getRandomPhoto()
        sharedPreference =
            requireContext().getSharedPreferences("Search_value", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = UnsplashPhotoAdapter(this)
        _binding = FragmentGalleryBinding.bind(view)

        binding.apply {

            viewModel.liveDataRandomPhoto.observe(viewLifecycleOwner) {
                if (it != null) {
                    Glide.with(requireActivity())
                        .load(it.urls?.regular?.convertedUrl)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
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
            viewModel.photos.observe(viewLifecycleOwner) { data ->
                data?.let {
                    adapter.submitData(viewLifecycleOwner.lifecycle, data)
                }
            }

            adapter.addLoadStateListener { loadState ->
                binding.apply {
                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                    recyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
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

            buttonRetry.setOnClickListener {
                adapter.retry()
            }
            imgClean.setOnClickListener {
                edtSearch.setText("")
                imgClean.isVisible = false
            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    imgClean.isVisible = edtSearch.text.toString() != ""
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }
            })
            edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val inputMethodManager =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    performSearch()
                    recyclerView.verticalScrollbarPosition = 0
                    return@OnEditorActionListener true
                }
                false
            })

        }

    }

    private fun performSearch() {
        if (binding.edtSearch.text.toString().isEmpty()) {
            binding.edtSearch.error = getString(R.string.string_error_edittext_search)
        } else {
            val editor = sharedPreference?.edit()
            if (sharedPreference?.getString("search", "").equals("")) {
                editor?.putString("search", binding.edtSearch.text.toString())
                editor?.apply()
                viewModel.searchPhotos(binding.edtSearch.text.toString())
                Sentry.configureScope { scope ->
                    scope.setTag("search_box", binding.edtSearch.text.toString())
                }
            } else {
                if (!sharedPreference?.getString("search", "")
                        .equals(binding.edtSearch.text.toString())
                ) {
                    editor?.putString("search", binding.edtSearch.text.toString())
                    editor?.apply()
                    viewModel.searchPhotos(binding.edtSearch.text.toString())
                    Sentry.configureScope { scope ->
                        scope.setTag("search_box", binding.edtSearch.text.toString())
                    }
                }
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        if (checkIsConnection()) {
            val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
            findNavController().navigate(action)
        } else {
            alertNetworkDialog(requireContext())
        }
    }

    fun checkIsConnection(): Boolean {
        val connectionManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (return wifiConnection?.isConnectedOrConnecting == true || (mobileDataConnection?.isConnectedOrConnecting == true)) true


    }

    private fun alertNetworkDialog(context: Context) {
        val dialog = Dialog(context, R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_network_alert)
        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)

        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

}