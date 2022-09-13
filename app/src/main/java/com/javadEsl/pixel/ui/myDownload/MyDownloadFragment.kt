package com.javadEsl.pixel.ui.myDownload

import android.Manifest
import android.R
import android.animation.ObjectAnimator
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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.javadEsl.pixel.databinding.FragmentMyDownloadBinding
import com.javadEsl.pixel.databinding.LayoutBottomSheetPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonCancellable.start
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class MyDownloadFragment : Fragment(com.javadEsl.pixel.R.layout.fragment_my_download),
    MyDownloadAdapter.OnItemClickListener {
    private val viewModel by viewModels<MyDownloadViewModel>()
    private var _binding: FragmentMyDownloadBinding? = null
    private val binding get() = _binding!!
    private var permissionType = "Start"

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                when (permissionType) {
                    "Start" -> {
                        getFileGallery()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                resources.getString(com.javadEsl.pixel.R.string.string_my_download_title)

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
        val dialog = BottomSheetDialog(
            requireContext(),
            com.javadEsl.pixel.R.style.AppBottomSheetDialogTheme
        )

        val sheetDialog =
            LayoutBottomSheetPhotoBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(sheetDialog.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isDraggable = false

        sheetDialog.apply {

            Glide.with(requireContext())
                .load(photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)

            cardViewShare.setOnClickListener {
                permissionType = "Share"
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return@setOnClickListener
                }
                val drawable = imageView.drawable
                val bitmap = drawable?.toBitmap()
                if (bitmap != null) {
                    saveImage(bitmap)?.let { Uri ->
                        shareImageUri(Uri)
                    }
                }
            }

            cardViewDelete.setOnClickListener {
                permissionType = "Delete"

                if (cardDeleteAlert.visibility == View.GONE) {
//                    val anim = ObjectAnimator.ofFloat(lyOption, "translationY", 100F, 0F).setDuration(500)
//                    anim.start()
                    cardDeleteAlert.visibility = View.VISIBLE
                }

            }

            textCancel.setOnClickListener {
                if (cardDeleteAlert.visibility == View.VISIBLE) {
                    cardDeleteAlert.visibility = View.GONE
                }
            }

            textDelete.setOnClickListener {

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

            cardViewBackToolbarBottomSheet.setOnClickListener {
                dialog.dismiss()
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