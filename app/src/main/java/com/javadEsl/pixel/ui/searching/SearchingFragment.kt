package com.javadEsl.pixel.ui.searching

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
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
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.databinding.FragmentSearchBinding
import com.javadEsl.pixel.fadeOut
import com.javadEsl.pixel.hide
import com.javadEsl.pixel.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchingFragment : Fragment(R.layout.fragment_search),
    SuggestPhotoAdapter.OnItemClickListener,
    PreviousSearchAdapter.OnItemClickListener,
    UnsplashPhotoAdapter.OnItemClickListener {
    private val viewModel by viewModels<SearchingViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var previousSearchArrayList: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.clearSearchStatus()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        val adapter = UnsplashPhotoAdapter(this, requireActivity())
        binding.apply {

            setStatusBarConfig()

            getSuggestion()

            setSearchConfig(adapter)

            edtSearch.addTextChangedListener {
                viewModel.getAutocomplete(it.toString())
            }

            imgClean.setOnClickListener {
                viewModel.clearSearchStatus()
                edtSearch.setText("")
                imgClean.isVisible = false
                layoutSuggestionList.show()
                layoutSearchList.hide()
                textViewEmpty.hide()
                buttonRetry.hide()
                textViewError.hide()
                viewModel.searchJob?.cancel()

                val set: MutableSet<String>? = viewModel.getSearchValueFromCash().first
                if (!set.isNullOrEmpty()) {

                    previousSearchArrayList = ArrayList(set)

                    val count =
                        if (previousSearchArrayList.size in 6..9) 2 else if (previousSearchArrayList.size in 9..19) 3 else 1

                    val layoutManager = StaggeredGridLayoutManager(count, RecyclerView.HORIZONTAL)
                    layoutManager.reverseLayout = true
                    val adapter =
                        PreviousSearchAdapter(this@SearchingFragment, previousSearchArrayList)
                    recPreviousSearch.adapter = adapter
                    recPreviousSearch.layoutManager = layoutManager

                    layoutPreviousSearchList.show()
                } else {
                    layoutPreviousSearchList.hide()
                }
            }

            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    imgClean.isVisible = edtSearch.text.toString() != ""

                    if (s.toString().isEmpty()) {
                        viewModel.clearSearchStatus()
                        layoutSuggestionList.show()
                        layoutSearchList.hide()
                        textViewEmpty.hide()
                        buttonRetry.hide()
                        textViewError.hide()
                        viewModel.searchJob?.cancel()

                        if (previousSearchArrayList.isNotEmpty()) {
                            layoutPreviousSearchList.show()
                        }
                    }
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
            edtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val inputMethodManager =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

                    adapter.addLoadStateListener { loadState ->
                        binding.apply {
                            shrimmerViewContanerSearch.isVisible =
                                loadState.source.refresh is LoadState.Loading
                            recSearching.isVisible =
                                loadState.source.refresh is LoadState.NotLoading
                            layoutSearch.isVisible =
                                loadState.source.refresh is LoadState.NotLoading
                            layoutSearch.isVisible =
                                loadState.source.refresh is LoadState.NotLoading
                            textViewError.isVisible = loadState.source.refresh is LoadState.Error
                            buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                                shrimmerViewContanerSearch.isVisible = false
                                recSearching.isVisible = false
                                textViewEmpty.isVisible = true
                                if (shrimmerViewContanerSearch.isShimmerStarted) shrimmerViewContanerSearch.stopShimmer()
                            } else {
                                textViewEmpty.isVisible = false
                            }
                        }
                    }
                    performSearch()

                    shrimmerViewContanerSearch.show()
                    shrimmerViewContanerSearch.startShimmer()
                    layoutSearchList.show()
                    layoutPreviousSearchList.hide()
                    layoutSuggestionList.hide()

                    recSearching.verticalScrollbarPosition = 0
                    return@OnEditorActionListener true
                }
                false
            })

            cardViewSearch.setOnClickListener {

                val inputMethodManager =
                    requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

                adapter.addLoadStateListener { loadState ->
                    binding.apply {
                        shrimmerViewContanerSearch.isVisible =
                            loadState.source.refresh is LoadState.Loading
                        recSearching.isVisible = loadState.source.refresh is LoadState.NotLoading
                        layoutSearch.isVisible = loadState.source.refresh is LoadState.NotLoading
                        textViewError.isVisible = loadState.source.refresh is LoadState.Error
                        buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                            shrimmerViewContanerSearch.isVisible = false
                            recSearching.isVisible = false
                            textViewEmpty.isVisible = true
//                            if (shrimmerViewContanerSearch.isShimmerStarted) shrimmerViewContanerSearch.stopShimmer()
                        } else {
                            textViewEmpty.isVisible = false
                        }
                    }
                }

                performSearch()

                shrimmerViewContanerSearch.show()
                shrimmerViewContanerSearch.startShimmer()
                layoutSearchList.show()
                layoutPreviousSearchList.hide()
                layoutSuggestionList.hide()

                recSearching.verticalScrollbarPosition = 0
            }

            textViewClearPrevious.setOnClickListener {
                viewModel.clearSearchCash()
                previousSearchArrayList.clear()
                layoutPreviousSearchList.fadeOut()
            }

            cardViewBackToolbarSearch.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }

    }

    private fun getSuggestion() = binding.apply {
        val set: MutableSet<String>? = viewModel.getSearchValueFromCash().first
        if (!set.isNullOrEmpty()) {
            previousSearchArrayList = ArrayList(set)
            val count =
                if (previousSearchArrayList.size in 6..9) 2 else if (previousSearchArrayList.size in 9..19) 3 else 1
            val layoutManager = StaggeredGridLayoutManager(count, RecyclerView.HORIZONTAL)
            layoutManager.reverseLayout = true
            val previousAdapter =
                PreviousSearchAdapter(this@SearchingFragment, previousSearchArrayList)
            recPreviousSearch.adapter = previousAdapter
            recPreviousSearch.layoutManager = layoutManager

        }

        if (!viewModel.getSearchStatus().equals("")) {
            layoutPreviousSearchList.hide()
            layoutSuggestionList.hide()
            layoutSearchList.show()
        } else {
            if (previousSearchArrayList.isNotEmpty()) {
                layoutPreviousSearchList.show()
            }
            layoutSearchList.hide()
            layoutSuggestionList.show()
        }

        shrimmerViewContanerSuggestion.show()
        shrimmerViewContanerSuggestion.startShimmer()
        viewModel.suggestPhotos()

        viewModel.liveDataSuggestPhoto.observe(viewLifecycleOwner) { data ->
            data?.let {
                val suggestAdapter =
                    SuggestPhotoAdapter(
                        data.shuffled(),
                        this@SearchingFragment
                    )
                recSuggest.itemAnimator = null
                val suggestLayoutManager =
                    StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
                suggestLayoutManager.reverseLayout = true
                recSuggest.layoutManager = suggestLayoutManager
                recSuggest.adapter = suggestAdapter
                layoutSuggestion.show()
                textViewEmpty.hide()
                buttonRetry.hide()
                textViewError.hide()
                shrimmerViewContanerSuggestion.hide()
                shrimmerViewContanerSuggestion.stopShimmer()
            }
        }
    }

    private fun setSearchConfig(adapter: UnsplashPhotoAdapter) = binding.apply {

        recSearching.itemAnimator = null
        recSearching.adapter = adapter.withLoadStateHeaderAndFooter(
            header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
            footer = UnsplashPhotoLoadStateAdapter { adapter.retry() },
        )

        viewModel.liveDataAutocomplete.observe(viewLifecycleOwner) {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                requireActivity(),
                R.layout.select_autocomplete_item,
                R.id.text_title,
                it?.map { it.query } ?: emptyList()
            )
            edtSearch.threshold = 1
            edtSearch.setAdapter(adapter)
        }

        viewModel.photos.observe(viewLifecycleOwner) { data ->
            data?.let {
                adapter.submitData(viewLifecycleOwner.lifecycle, data)
            }
        }
    }

    private fun setStatusBarConfig() {
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
                binding.imageViewSearch.setColorFilter(R.color.color_background_fragments)
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
    }

    private fun performSearch() {
        if (binding.edtSearch.text.toString().isEmpty()) {
            binding.edtSearch.error = getString(R.string.string_error_edittext_search)
        } else {
            setSearchCash()
            viewModel.searchPhotos(binding.edtSearch.text.toString())
            binding.recSearching.scrollToPosition(0)
        }
    }

    private fun setSearchCash() {
        if (viewModel.getSearchStatus().equals("")
            ||
            viewModel.getSearchStatus() != binding.edtSearch.text.toString()
        ) {
            viewModel.setSuccessSearchInCash(
                binding.edtSearch.text.toString(),
                previousSearchArrayList
            )
        }
    }

    override fun onItemClick(photo: PixelPhoto) {
        if (checkIsConnection()) {
            if (photo.isAdvertisement) return
            val action = SearchingFragmentDirections.actionSearchingFragmentToDetailsFragment(
                photo.id,
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
        val dialog = Dialog(context, R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(com.javadEsl.pixel.R.layout.layout_dialog_network_alert)
        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)

        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(suggest: String) {
        binding.apply {
            edtSearch.setText(suggest)
            cardViewSearch.performClick()
        }
    }

    override fun onItemClick(photo: AllPhotosItem) {

        if (checkIsConnection()) {
            if (photo.isAdvertisement) return
            val action = SearchingFragmentDirections.actionSearchingFragmentToDetailsFragment(
                photo.id.toString(),
                userName = photo.user?.username.toString()
            )
            findNavController().navigate(action)
        } else {
            alertNetworkDialog(requireContext())
        }

    }

}