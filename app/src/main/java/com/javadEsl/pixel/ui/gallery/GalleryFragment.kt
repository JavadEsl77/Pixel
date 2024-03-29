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
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.model.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.model.search.convertedUrl
import com.javadEsl.pixel.data.model.topics.TopicsModelItem
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import com.javadEsl.pixel.helper.extensions.*
import com.javadEsl.pixel.service.WallpaperWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class GalleryFragment :
    Fragment(R.layout.fragment_gallery),
    TopicsAdapter.OnItemClickListener,
    AllPhotoAdapter.OnItemClickListener, TopicsPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private var emptyDataReceiver = false
    private val binding get() = _binding!!
    private lateinit var allPhotoAdapter: AllPhotoAdapter
    private lateinit var topicsPhotoAdapter: TopicsPhotoAdapter
    private lateinit var topicsAdapter: TopicsAdapter
    private lateinit var listTopics:List<TopicsModelItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        allPhotoAdapter = AllPhotoAdapter(this, requireActivity())
        topicsPhotoAdapter = TopicsPhotoAdapter(this, requireActivity())
        viewModel.topicsList()

    }

    //در اپدیت جدید از سرویس ثبت خود کار والپیپر استفاده کن
    private fun myPeriodicWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val myRequest = PeriodicWorkRequest.Builder(
            WallpaperWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .addTag("my_id")
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork(
                "my_id",
                ExistingPeriodicWorkPolicy.KEEP,
                myRequest
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)
        setupViews()
        observe()
    }

    private fun observe() = binding.apply {
        shrimmerViewContaner.startShimmer()
        viewModel.liveDataTopics.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    buttonRetry.hide()
                    textViewError.hide()
                    emptyDataReceiver = false
                    val topicIdAndPosition = viewModel.getTopicIdAndPosition(it)
                    listTopics = it
                    topicsAdapter = TopicsAdapter(
                        this@GalleryFragment,
                        it,
                        topicIdAndPosition.second
                    )
                    recTopics.adapter = topicsAdapter
                    toolbarTopics.fadeIn()
                    toolbarHome.fadeIn()


                    recTopics.scrollToPosition(topicIdAndPosition.second)

                    val topicId = topicIdAndPosition.first
                    if (topicId == TopicsModelItem.Type.USER) {
                        layoutRecommended.show()
                        textViewTitleTopicCover.text = "تازه ترین ها"
                        textViewDescriptionTopicCover.text =
                            "بیش از 3 میلیون تصویر با وضوح بالا رایگان توسط سخاوتمندترین جامعه عکاسان جهان برای شما آورده شده است."
                        getRecommendedData()
                        Glide.with(requireContext())
                            .load(R.drawable.img_splash)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .error(R.drawable.ic_error_photos)
                            .into(binding.imageViewTopicCover)
                    } else {
                        layoutTopics.show()
                        setCoverImage(topicIdAndPosition.second, it)
                        if (topicsPhotoAdapter.itemCount <= 0) {
                            viewModel.allTopicPhotos(topicId)
                            collapsBanner.expand()
                        }
                        topicsPhotoAdapter.addLoadStateListener { loadState ->
                            binding.apply {
                                shrimmerViewContaner.isVisible =
                                    loadState.source.refresh is LoadState.NotLoading
                                shrimmerViewContaner.isVisible =
                                    loadState.source.refresh is LoadState.Loading
                                recyclerViewTopics.isVisible =
                                    loadState.source.refresh is LoadState.NotLoading
                                layoutGallery.isVisible =
                                    loadState.source.refresh is LoadState.NotLoading
                                textViewError.isVisible =
                                    loadState.source.refresh is LoadState.Error
                                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && topicsPhotoAdapter.itemCount < 1) {
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

        viewModel.topicsPhotos.observe(viewLifecycleOwner) {
            it.let {
                topicsPhotoAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

    }

    private fun setupViews() = binding.apply {
        collapsBanner.collapse()
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

        recyclerViewRecommended.itemAnimator = null
        recyclerViewRecommended.adapter = allPhotoAdapter.withLoadStateHeaderAndFooter(
            header = AllPhotoLoadStateAdapter { allPhotoAdapter.retry() },
            footer = AllPhotoLoadStateAdapter { allPhotoAdapter.retry() },
        )

        recyclerViewTopics.itemAnimator = null
        recyclerViewTopics.adapter = topicsPhotoAdapter.withLoadStateHeaderAndFooter(
            header = TopicsPhotoLoadStateAdapter { topicsPhotoAdapter.retry() },
            footer = TopicsPhotoLoadStateAdapter { topicsPhotoAdapter.retry() },
        )

        buttonRetry.setOnClickListener {
            viewModel.topicsList()
            shrimmerViewContaner.show()
            shrimmerViewContaner.startShimmer()
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

//            val action = GalleryFragmentDirections.actionGalleryFragmentToAuthFragment()
//            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRecommendedData() = binding.apply {
        layoutRecommended.show()
        layoutTopics.hide()

        viewModel.newPhotos.observe(viewLifecycleOwner) {
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
        binding.apply {
            if (topicsModelItem.id != viewModel.getTopicIdAndPosition(listTopics).first) {
                shrimmerViewContaner.show()
                shrimmerViewContaner.startShimmer()
                layoutRecommended.hide()
                if (topicsModelItem.id == TopicsModelItem.Type.USER) {
                    textViewTitleTopicCover.text = "تازه ترین ها"
                    textViewDescriptionTopicCover.text =
                        "بیش از 3 میلیون تصویر با وضوح بالا رایگان توسط سخاوتمندترین جامعه عکاسان جهان برای شما آورده شده است."
                    Glide.with(requireContext())
                        .load(R.drawable.img_splash)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_error_photos)
                        .into(binding.imageViewTopicCover)
                    getRecommendedData()
                    collapsBanner.expand()
                    recyclerViewRecommended.scrollToPosition(0)
                } else {
                    layoutRecommended.hide()
                    collapsBanner.expand()
                    setCoverImage(topicsModelItem)
                    viewModel.allTopicPhotos(topicsModelItem.id)
                    layoutTopics.show()
                    recyclerViewTopics.scrollToPosition(0)
                }
            }
        }
        viewModel.onTopicItemClick(topicsModelItem, position)
    }

    private fun setCoverImage(topicsModelItem: TopicsModelItem) = binding.apply {
        if (topicsModelItem.coverPhoto?.premium != true) {
            val urlCover = topicsModelItem.coverPhoto?.urls?.regular
            Glide.with(requireContext())
                .load(urlCover?.convertedUrl)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error_photos)
                .into(binding.imageViewTopicCover)

        } else {
            Glide.with(requireContext())
                .load(R.drawable.img_splash)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_error_photos)
                .into(binding.imageViewTopicCover)
        }

        textViewTitleTopicCover.text = topicsModelItem.title
        textViewDescriptionTopicCover.text = topicsModelItem.description
    }

    private fun setCoverImage(position: Int, list: List<TopicsModelItem>) = binding.apply {
        val urlCover = list[position].coverPhoto?.urls?.regular
        Glide.with(requireContext())
            .load(urlCover?.convertedUrl)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_error_photos).into(binding.imageViewTopicCover)

        textViewTitleTopicCover.text = list[position].title
        textViewDescriptionTopicCover.text = list[position].description
    }

}
