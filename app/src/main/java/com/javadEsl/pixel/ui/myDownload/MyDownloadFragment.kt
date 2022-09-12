package com.javadEsl.pixel.ui.myDownload

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.FragmentMyDownloadBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class MyDownloadFragment : Fragment(com.javadEsl.pixel.R.layout.fragment_my_download),
    MyDownloadAdapter.OnItemClickListener {
    private val viewModel by viewModels<MyDownloadViewModel>()
    private var _binding: FragmentMyDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyDownloadBinding.bind(view)
        val adapter = MyDownloadAdapter(viewModel.getDownloadPictures(), this)

        binding.apply {

            if (adapter.downloadList.isEmpty()) {
                recyclerViewMyDownload.isVisible = false
            } else {
                recyclerViewMyDownload.setHasFixedSize(true)
                recyclerViewMyDownload.itemAnimator = null
                recyclerViewMyDownload.adapter = adapter

            }
        }
    }

    override fun onItemClick(photo: File) {
        showDialogOne(photo)
    }

    private fun showDialogOne(photo: File) {
        val dialog = BottomSheetDialog(requireContext(),R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(R.layout.layout_bottom_sheet_photo)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isDraggable = false

        val imageView = dialog.findViewById<ImageView>(com.javadEsl.pixel.R.id.image_view)

        if (imageView != null) {
            Glide.with(requireContext())
                .load(photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }

        dialog.show()
    }


}