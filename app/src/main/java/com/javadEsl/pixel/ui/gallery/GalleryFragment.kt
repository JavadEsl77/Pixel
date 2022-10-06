package com.javadEsl.pixel.ui.gallery

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log


@AndroidEntryPoint
class GalleryFragment :
    Fragment(R.layout.fragment_gallery),
    TopicsAdapter.OnItemClickListener,
    AllPhotoAdapter.OnItemClickListener {
    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.topicsList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val allPhotoAdapter = AllPhotoAdapter(this, requireActivity())
        _binding = FragmentGalleryBinding.bind(view)

        binding.apply {

            val nightModeFlags = requireActivity().resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    val wic = WindowInsetsControllerCompat(
                        requireActivity().window,
                        requireActivity().window.decorView
                    )
                    wic.isAppearanceLightStatusBars = false
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    val wic = WindowInsetsControllerCompat(
                        requireActivity().window,
                        requireActivity().window.decorView
                    )
                    wic.isAppearanceLightStatusBars = true
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {

                }
            }

            requireActivity().window.statusBarColor = ContextCompat.getColor(
                requireActivity(),
                R.color.status_bar_color
            )


            viewModel.liveDataTopics.observe(viewLifecycleOwner) {
                it.let {

                    val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                    layoutManager.reverseLayout = false
                    val topicsAdapter = TopicsAdapter(this@GalleryFragment, it,requireActivity())
                    recTopics.adapter = topicsAdapter
                    recTopics.layoutManager= layoutManager
                }
            }


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

    override fun onItemClick() {
        TODO("Not yet implemented")
    }


}