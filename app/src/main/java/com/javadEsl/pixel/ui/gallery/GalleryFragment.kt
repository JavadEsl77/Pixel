package com.javadEsl.pixel.ui.gallery

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.search.convertedUrl
import com.javadEsl.pixel.data.topics.TopicsModelItem
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import com.javadEsl.pixel.fadeIn
import com.javadEsl.pixel.hide
import com.javadEsl.pixel.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment :
    Fragment(R.layout.fragment_gallery),
    TopicsAdapter.OnItemClickListener,
    AllPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private var emptyDataReceiver = false
    private val binding get() = _binding!!
    private lateinit var allPhotoAdapter: AllPhotoAdapter
    private lateinit var topicsAdapter: TopicsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allPhotoAdapter = AllPhotoAdapter(this, requireActivity())
        viewModel.topicsList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)

        binding.apply {


            val nightModeFlags = requireActivity().resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES       -> {
                    val wic = WindowInsetsControllerCompat(
                        requireActivity().window,
                        requireActivity().window.decorView
                    )
                    wic.isAppearanceLightStatusBars = false
                }
                Configuration.UI_MODE_NIGHT_NO        -> {
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

            getTopicData()

            buttonRetry.setOnClickListener {
                if (emptyDataReceiver) {
                    getTopicData()
                } else {
                    allPhotoAdapter.retry()
                }

            }

            cardViewSearching.setOnClickListener {
                val action =
                    GalleryFragmentDirections.actionGalleryFragmentToSearchingFragment()
                findNavController().navigate(action)
            }

            cardMyDownload.setOnClickListener {
                val action =
                    GalleryFragmentDirections.actionGalleryFragmentToMyDownloadFragment()
                findNavController().navigate(action)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTopicData() = binding.apply {
        shrimmerViewContaner.show()
        shrimmerViewContaner.startShimmer()
        viewModel.liveDataTopics.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    layoutTopics.show()
                    layoutRecommended.show()
                    buttonRetry.hide()
                    textViewError.hide()
                    emptyDataReceiver = false

                    val layoutManager =
                        LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                    topicsAdapter = TopicsAdapter(
                        this@GalleryFragment,
                        it,
                        viewModel.getTopicIdAndPosition().second
                    )
                    recTopics.adapter = topicsAdapter
                    recTopics.layoutManager = layoutManager
                    toolbarTopics.fadeIn()
                    toolbarHome.fadeIn()
                    shrimmerViewContaner.hide()
                    val topicIdAndPosition = viewModel.getTopicIdAndPosition()
                    recTopics.scrollToPosition(topicIdAndPosition.second)

                    val topicId = topicIdAndPosition.first
                    if (topicId == TopicsModelItem.Type.USER) {
                        getRecommendedData()
                    } else {
                        getTopicPhotoList(allPhotoAdapter, topicId)
                    }
                } else {
                    layoutTopics.hide()
                    layoutRecommended.hide()
                    shrimmerViewContaner.hide()
                    buttonRetry.show()
                    textViewError.show()
                    emptyDataReceiver = true
                }
            }
        }
    }

    private fun getRecommendedData() = binding.apply {
        layoutRecommended.show()
        layoutTopics.hide()

        recyclerViewRecommended.itemAnimator = null
        recyclerViewRecommended.adapter = allPhotoAdapter.withLoadStateHeaderAndFooter(
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
                recyclerViewRecommended.isVisible =
                    loadState.source.refresh is LoadState.NotLoading
                layoutGallery.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && allPhotoAdapter.itemCount < 1) {
                    shrimmerViewContaner.isVisible = false
                    recyclerViewRecommended.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }
    }

    private fun getTopicPhotoList(allPhotoAdapter: AllPhotoAdapter, topicId: String) {
        binding.apply {
            layoutRecommended.hide()
            layoutTopics.show()

            recyclerViewTopics.itemAnimator = null
            recyclerViewTopics.adapter = allPhotoAdapter.withLoadStateHeaderAndFooter(
                header = AllPhotoLoadStateAdapter { allPhotoAdapter.retry() },
                footer = AllPhotoLoadStateAdapter { allPhotoAdapter.retry() },
            )

            viewModel.topicPhotos(topicId).observe(viewLifecycleOwner) {
                it.let {
                    allPhotoAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }

                allPhotoAdapter.addLoadStateListener { loadState ->
                    binding.apply {
                        shrimmerViewContaner.isVisible =
                            loadState.source.refresh is LoadState.NotLoading
                        shrimmerViewContaner.isVisible =
                            loadState.source.refresh is LoadState.Loading
                        recyclerViewTopics.isVisible =
                            loadState.source.refresh is LoadState.NotLoading
                        layoutGallery.isVisible =
                            loadState.source.refresh is LoadState.NotLoading
                        textViewError.isVisible = loadState.source.refresh is LoadState.Error
                        buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && allPhotoAdapter.itemCount < 1) {
                            recyclerViewTopics.isVisible = false
                            shrimmerViewContaner.isVisible = false
                            if (shrimmerViewContaner.isShimmerStarted) shrimmerViewContaner.stopShimmer()
                            textViewEmpty.isVisible = true
                        } else {
                            textViewEmpty.isVisible = false
                        }
                    }
                }
            }
        }

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
        val mobileDataConnection =
            connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

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

    override fun onTopicsItemClick(topicsModelItem: TopicsModelItem, position: Int) {

        if (topicsModelItem.id != viewModel.getTopicIdAndPosition().first) {
            if (topicsModelItem.id == TopicsModelItem.Type.USER) {
                getRecommendedData()
            } else {
                val coverUrl = topicsModelItem.coverPhoto?.urls?.small.toString()
                Glide.with(requireContext())
                    .load(coverUrl.convertedUrl)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error_photos)
                    .into(binding.imageViewTopicCover)

                getTopicPhotoList(allPhotoAdapter, topicsModelItem.id)
            }
        }
        viewModel.onTopicItemClick(topicsModelItem, position)
    }

}