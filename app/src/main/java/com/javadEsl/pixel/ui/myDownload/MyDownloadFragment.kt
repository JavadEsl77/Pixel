package com.javadEsl.pixel.ui.myDownload

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.FragmentMyDownloadBinding
import com.javadEsl.pixel.size
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class MyDownloadFragment : Fragment(R.layout.fragment_my_download),
    MyDownloadAdapter.OnItemClickListener {
    private val viewModel by viewModels<MyDownloadViewModel>()
    private var _binding: FragmentMyDownloadBinding? = null
    private var permissionType = "Start"
    private val binding get() = _binding!!

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                when (permissionType) {
                    "Share"  -> {

                    }
                    "Delete" -> {

                    }
                    "Start"  -> {
                        getFileGallery()
                    }
                }

            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyDownloadBinding.bind(view)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        getFileGallery()
    }

    private fun getFileGallery() {

        if (isRequireExternalStorageManager()) {
            requestPermission()
            return
        }

        val adapter = MyDownloadAdapter(viewModel.getDownloadPictures(), this)
        binding.apply {

            layoutToolbar.textViewTitleToolbarScreens.text =
                resources.getString(R.string.string_my_download_title)

            layoutToolbar.cardViewBackToolbarScreens.setOnClickListener {
                findNavController().popBackStack()
            }

            if (adapter.downloadList.isEmpty()) {
                recyclerViewMyDownload.isVisible = false
                layoutMyDownloadError.isVisible = true
            } else {
                recyclerViewMyDownload.isVisible = true
                layoutMyDownloadError.isVisible = false
                recyclerViewMyDownload.setHasFixedSize(true)
                recyclerViewMyDownload.itemAnimator = null
                recyclerViewMyDownload.adapter = adapter

            }
        }
    }

    override fun onItemClick(photo: File) {
        showDialogOne(photo)
    }

    private fun isRequireExternalStorageManager() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                && !Environment.isExternalStorageManager()

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermission() {
        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
            startActivity(this)
        }
    }

    private fun showDialogOne(photo: File) {
        val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(R.layout.layout_bottom_sheet_photo)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isDraggable = true

        val imageView = dialog.findViewById<ImageView>(R.id.image_view)
        val cardViewShare = dialog.findViewById<CardView>(R.id.card_view_share)
        val cardViewDelete = dialog.findViewById<CardView>(R.id.card_view_delete)

        if (imageView != null) {
            Glide.with(requireContext())
                .load(photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }

        cardViewShare?.setOnClickListener {
            permissionType = "Share"
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return@setOnClickListener
            }
            val drawable = imageView?.drawable
            val bitmap = drawable?.toBitmap()
            if (bitmap != null) {
                saveImage(bitmap)?.let { Uri ->
                    shareImageUri(Uri)
                }
            }
        }

        cardViewDelete?.setOnClickListener {
            permissionType = "Delete"

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return@setOnClickListener
            }



            if (photo.exists()) {
                photo.delete()
                dialog.dismiss()
                getFileGallery()
            }


        }

        dialog.show()
    }

    private fun saveImage(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder: File = File(requireContext().cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.jpg")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                file
            );
        } catch (e: IOException) {
            Log.d(
                ContentValues.TAG,
                "IOException while trying to write file for sharing: " + e.message
            )
        }
        return uri
    }

    private fun shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }

}