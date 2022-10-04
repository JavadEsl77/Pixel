package com.javadEsl.pixel.ui.searching

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
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.javadEsl.pixel.R
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.data.search.convertedUrl
import com.javadEsl.pixel.databinding.FragmentGalleryBinding
import com.javadEsl.pixel.databinding.FragmentSearchBinding
import com.javadEsl.pixel.isBrightColor
import com.javadEsl.pixel.ui.gallery.GalleryFragmentDirections
import com.javadEsl.pixel.ui.gallery.UnsplashPhotoAdapter
import com.javadEsl.pixel.ui.gallery.UnsplashPhotoLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchingFragment : Fragment(R.layout.fragment_search), UnsplashPhotoAdapter.OnItemClickListener {
    private val viewModel by viewModels<SearchingViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var sharedPreference: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getRandomPhoto()
        sharedPreference = requireContext().getSharedPreferences("Search_value", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        val adapter = UnsplashPhotoAdapter(this, requireActivity())
        binding.apply {

            if (Color.parseColor("#000000").isBrightColor) {
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
                "#" + "000000".replace(
                    "#",
                    ""
                )
            )

            recSearching.itemAnimator = null
            recSearching.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() },
            )

            viewModel.liveDataRandomPhoto.observe(viewLifecycleOwner) {
                if (it != null) {

                    Glide.with(requireActivity())
                        .load(it.urls?.regular?.convertedUrl)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(com.javadEsl.pixel.R.drawable.img)
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
            edtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val inputMethodManager =
                        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    performSearch()
                    recSearching.verticalScrollbarPosition = 0
                    return@OnEditorActionListener true
                }
                false
            })
        }

    }

    private fun performSearch() {
        if (binding.edtSearch.text.toString().isEmpty()) {
            binding.edtSearch.error =
                getString(com.javadEsl.pixel.R.string.string_error_edittext_search)
        } else {
            val editor = sharedPreference?.edit()
            if (sharedPreference?.getString("search", "").equals("")) {
                editor?.putString("search", binding.edtSearch.text.toString())
                editor?.apply()
                viewModel.searchPhotos(binding.edtSearch.text.toString())
                binding.recSearching.scrollToPosition(0)
            } else {
                if (!sharedPreference?.getString("search", "")
                        .equals(binding.edtSearch.text.toString())
                ) {
                    editor?.putString("search", binding.edtSearch.text.toString())
                    editor?.apply()
                    viewModel.searchPhotos(binding.edtSearch.text.toString())
                    binding.recSearching.scrollToPosition(0)
                }
            }


        }
    }

    override fun onItemClick(photo: PixelPhoto) {
        if (checkIsConnection()) {
            if (photo.isAdvertisement) return
            val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo.id, userName = photo.user?.username.toString())
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

}