package com.javadEsl.pixel.ui.gallery

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.data.search.convertedUrl
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import com.javadEsl.pixel.isBrightColor
import com.javadEsl.pixel.ui.searching.SearchingViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GalleryFragment :
    Fragment(R.layout.fragment_gallery),
    AllPhotoAdapter.OnItemClickListener {
    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val allPhotoAdapter = AllPhotoAdapter(this, requireActivity())
        _binding = FragmentGalleryBinding.bind(view)

        binding.apply {


            if (Color.parseColor("#ffffff").isBrightColor) {
                val wic = WindowInsetsControllerCompat(
                    requireActivity().window,
                    requireActivity().window.decorView
                )
                wic.isAppearanceLightStatusBars = true;
            } else {
                val wic = WindowInsetsControllerCompat(
                    requireActivity().window,
                    requireActivity().window.decorView
                )
                wic.isAppearanceLightStatusBars = false
            }

            requireActivity().window.statusBarColor = Color.parseColor(
                "#" + "ffffff".replace(
                    "#",
                    ""
                )
            )

            recyclerView.itemAnimator = null
            recyclerView.adapter = allPhotoAdapter.withLoadStateHeaderAndFooter(
                header = AllPhotoLoadStateAdapter { allPhotoAdapter.retry() },
                footer = AllPhotoLoadStateAdapter { allPhotoAdapter.retry() },
            )


            viewModel.allPhotos.observe(viewLifecycleOwner) {
                it.let {
                    allPhotoAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }

            }
            allPhotoAdapter.addLoadStateListener { loadState ->
                binding.apply {
                    loadingAnimView.isVisible = loadState.source.refresh is LoadState.Loading
                    recyclerView.isVisible =
                        loadState.source.refresh is LoadState.NotLoading
                    textViewError.isVisible = loadState.source.refresh is LoadState.Error
                    buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                    if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && allPhotoAdapter.itemCount < 1) {
                        recyclerView.isVisible = false
                        textViewEmpty.isVisible = true
                    } else {
                        textViewEmpty.isVisible = false
                    }
                }
            }

            buttonRetry.setOnClickListener {
                allPhotoAdapter.retry()
            }

            cardViewSearching.setOnClickListener {
                val action = GalleryFragmentDirections.actionGalleryFragmentToSearchingFragment()
                findNavController().navigate(action)
            }


            cardMyDownload.setOnClickListener {
                val action = GalleryFragmentDirections.actionGalleryFragmentToMyDownloadFragment()
                findNavController().navigate(action)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(photo: AllPhotosItem) {
        if (checkIsConnection()) {
            if (photo.isAdvertisement) return
            val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(
                photo.id.toString(),
                userName = photo.user?.username.toString()
            )
            findNavController().navigate(action)
        } else {
            alertNetworkDialog(requireContext())
        }
    }

    private fun checkIsConnection(): Boolean {
        val connectionManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (return wifiConnection?.isConnectedOrConnecting == true || (mobileDataConnection?.isConnectedOrConnecting == true)) true

    }

    private fun alertNetworkDialog(context: Context) {
        val dialog = Dialog(context, com.javadEsl.pixel.R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(com.javadEsl.pixel.R.layout.layout_dialog_network_alert)
        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)

        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }


}