package com.javadEsl.pixel.ui.searching

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
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
import com.javadEsl.pixel.*
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchingFragment : Fragment(R.layout.fragment_search),
    SuggestPhotoAdapter.OnItemClickListener,
    PreviousSearchAdapter.OnItemClickListener,
    UnsplashPhotoAdapter.OnItemClickListener {
    private val viewModel by viewModels<SearchingViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var sharedPreference: SharedPreferences? = null
    private var previousSearchArrayList: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference =
            requireContext().getSharedPreferences("Search_value", Context.MODE_PRIVATE)
        clearSearchCash()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        val adapter = UnsplashPhotoAdapter(this, requireActivity())
        binding.apply {

            suggestLoadingAnimView.show()

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
                    imageViewSearch.setColorFilter(R.color.color_background_fragments)
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

            val set: MutableSet<String>? = sharedPreference?.getStringSet("previousSearch", null)
            if (!set.isNullOrEmpty()) {
                previousSearchArrayList = ArrayList(set)
                val count =
                    if (previousSearchArrayList.size in 6..9) 2 else if (previousSearchArrayList.size in 9..19) 3 else 1
                val layoutManager = StaggeredGridLayoutManager(count, RecyclerView.HORIZONTAL)
                layoutManager.reverseLayout = true
                val adapter = PreviousSearchAdapter(this@SearchingFragment, previousSearchArrayList)
                recPreviousSearch.adapter = adapter
                recPreviousSearch.layoutManager = layoutManager

            }

            if (!sharedPreference?.getString("searchValue","").equals("")) {
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


            var defaultSearch = "iran"
            if(previousSearchArrayList.isNotEmpty()){
                val randomNumber = (0 until previousSearchArrayList.size).random()
                defaultSearch = previousSearchArrayList[randomNumber]
            }

            viewModel.suggestPhotos(defaultSearch)

            viewModel.liveDataSuggestPhoto.observe(viewLifecycleOwner) { data ->
                data?.let {
                    val suggestAdapter =
                        SuggestPhotoAdapter(data, this@SearchingFragment, requireActivity())
                    recSuggest.itemAnimator = null
                    val suggestLayoutManager =
                        StaggeredGridLayoutManager(1, RecyclerView.HORIZONTAL)
                    suggestLayoutManager.reverseLayout = true
                    recSuggest.layoutManager = suggestLayoutManager
                    recSuggest.adapter = suggestAdapter

                    suggestLoadingAnimView.hide()
                }
            }

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

            edtSearch.addTextChangedListener {
                viewModel.getAutocomplete(it.toString())
            }

            imgClean.setOnClickListener {
                edtSearch.setText("")
                clearSearchCash()
                imgClean.isVisible = false
                layoutSuggestionList.show()
                layoutSearchList.hide()
                val set: MutableSet<String>? =
                    sharedPreference?.getStringSet("previousSearch", null)
                if (!set.isNullOrEmpty()) {
                    previousSearchArrayList = ArrayList(set)
                    val count =
                        if (previousSearchArrayList.size in 6..9) 2 else if (previousSearchArrayList.size in 9..19) 3 else 1
                    val layoutManager = StaggeredGridLayoutManager(count, RecyclerView.HORIZONTAL)
                    layoutManager.reverseLayout = true;
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

                    Log.e("TAG", "onViewCreated: $s")
                    if (s.toString().isEmpty()){
                        clearSearchCash()
                        layoutSuggestionList.show()
                        layoutSearchList.hide()

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
                            loadingAnimView.isVisible =
                                loadState.source.refresh is LoadState.Loading
                            recSearching.isVisible =
                                loadState.source.refresh is LoadState.NotLoading
                            textViewError.isVisible = loadState.source.refresh is LoadState.Error
                            buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                                recSearching.isVisible = false
                                textViewEmpty.isVisible = true
                            } else {
                                textViewEmpty.isVisible = false
                            }
                        }
                    }
                    performSearch()

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
                        loadingAnimView.isVisible = loadState.source.refresh is LoadState.Loading
                        recSearching.isVisible =
                            loadState.source.refresh is LoadState.NotLoading
                        textViewError.isVisible = loadState.source.refresh is LoadState.Error
                        buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                        if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                            recSearching.isVisible = false
                            textViewEmpty.isVisible = true
                        } else {
                            textViewEmpty.isVisible = false
                        }
                    }
                }
                performSearch()

                layoutSearchList.show()
                layoutPreviousSearchList.hide()
                layoutSuggestionList.hide()

                recSearching.verticalScrollbarPosition = 0
            }

            textViewClearPrevious.setOnClickListener {
                sharedPreference?.edit()?.clear()?.apply()
                previousSearchArrayList.clear()
                layoutPreviousSearchList.fadeOut()
            }

            cardViewBackToolbarSearch.setOnClickListener {
                findNavController().popBackStack()
            }
        }

    }

    private fun performSearch() {
        if (binding.edtSearch.text.toString().isEmpty()) {
            binding.edtSearch.error = getString(R.string.string_error_edittext_search)
        } else {
            val editor = sharedPreference?.edit()
            if (!sharedPreference?.getString("search", "")
                    .equals(binding.edtSearch.text.toString())
            ) {
                val set: MutableSet<String> = HashSet()
                if (!previousSearchArrayList.contains(binding.edtSearch.text.toString())) {
                    previousSearchArrayList.add(binding.edtSearch.text.toString())
                    set.addAll(previousSearchArrayList)
                    editor?.putStringSet("previousSearch", set)
                    editor?.putString("searchValue", binding.edtSearch.text.toString())
                    editor?.apply()
                }
                viewModel.searchPhotos(binding.edtSearch.text.toString())
                binding.recSearching.scrollToPosition(0)
            }

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

     private fun clearSearchCash(){
         val editor = sharedPreference?.edit()
         editor?.putString("searchValue","")
         editor?.apply()
     }

    override fun onItemClick(suggest: String) {
        binding.apply {
            val editor = sharedPreference?.edit()
            editor?.putString("searchValue",suggest)
            editor?.apply()
            edtSearch.setText(suggest)
            cardViewSearch.performClick()
        }
    }

}