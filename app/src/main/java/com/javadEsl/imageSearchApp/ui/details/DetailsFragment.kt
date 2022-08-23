package com.javadEsl.imageSearchApp.ui.details

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.huxq17.download.Pump
import com.huxq17.download.config.DownloadConfig
import com.javadEsl.imageSearchApp.R
import com.javadEsl.imageSearchApp.data.ModelPhoto
import com.javadEsl.imageSearchApp.data.UnsplashPhoto
import com.javadEsl.imageSearchApp.data.UnsplashRepository
import com.javadEsl.imageSearchApp.data.convertedUrl
import com.javadEsl.imageSearchApp.databinding.FragmentDetailsBinding
import com.javadEsl.imageSearchApp.isBrightColor
import com.javadEsl.imageSearchApp.ui.gallery.UnsplashPhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details),
    UnsplashPhotoAdapter.OnItemClickListener, TodoAdapter.OnItemClickListener {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: ProgressDialog
    private var downloadStatus: Boolean = false
    private var modelPhoto: ModelPhoto? = null
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                modelPhoto?.let { model -> startDownloadImage(model) }
            }
        }

    @Inject
    lateinit var photo: UnsplashRepository
    private val viewModel by viewModels<DetailViewModel>()
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPhotoDetail(args.photo.id)
        viewModel.getUserPhotos(args.photo.user?.username.toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            initViewModel()
        }
    }

    private fun initViewModel() {

        viewModel.liveDataList.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            setDetail(it)
            modelPhoto = it
        }
    }

    private fun setDetail(modelPhoto: ModelPhoto) {
        binding.apply {
            getUserPhotos(binding)
            textViewUserName.text = modelPhoto.user?.name

            if (!modelPhoto.user?.bio.isNullOrEmpty()) {
                textViewBio.isVisible = true
                textViewTitleBio.isVisible = true
                textViewBio.text = modelPhoto.user?.bio
            }
            if (!modelPhoto.user?.instagramUsername.isNullOrEmpty()) {
                textViewInstagram.isVisible = true
                textViewTitleInstagram.isVisible = true
                textViewInstagram.text = modelPhoto.user?.instagramUsername
            }

            if (!modelPhoto.location?.name.isNullOrEmpty()) {
                layoutLocation.isVisible = true
                textViewTitleLocation.isVisible = true
                textViewLocation.isVisible = true
                textViewLocation.text = modelPhoto.location?.name
            }

            if (!modelPhoto.exif?.model.isNullOrEmpty()) {
                layoutCameraInfo.isVisible = true
                textViewTitleCamera.isVisible = true
                textViewCameraModel.isVisible = true
                textViewCameraModel.text = modelPhoto.exif?.make + " , " + modelPhoto.exif?.model
            }

            if (modelPhoto.views.toString().isNotEmpty()) {
                textViewTitlePhotoInfo.isVisible = true
                layoutViewInfo.isVisible = true
                textViewPhotoViews.isVisible = true
                textViewPhotoViews.text = modelPhoto.views.toString()
            }

            if (modelPhoto.downloads.toString().isNotEmpty()) {
                textViewTitlePhotoInfo.isVisible = true
                layoutDownloadInfo.isVisible = true
                textViewPhotoDownload.isVisible = true
                textViewPhotoDownload.text = modelPhoto.downloads.toString()
            }

            if (modelPhoto.likes.toString().isNotEmpty()) {
                layoutLikeInfo.isVisible = true
                textViewPhotoLikes.isVisible = true
                textViewPhotoLikes.text = modelPhoto.likes.toString()
            }

            Glide.with(this@DetailsFragment)
                .load(modelPhoto.user?.profileImage?.large?.convertedUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(com.javadEsl.imageSearchApp.R.drawable.ic_user)
                .into(imageViewProfile)


            cardViewColor.setCardBackgroundColor(
                Color.parseColor(
                    "#cc" + modelPhoto.color?.replace(
                        "#",
                        ""
                    )
                )
            )
            cardDownload.setCardBackgroundColor(
                Color.parseColor(
                    "#cc" + modelPhoto.color?.replace(
                        "#",
                        ""
                    )
                )
            )
            cardWallpaper.setCardBackgroundColor(
                Color.parseColor(
                    "#cc" + modelPhoto.color?.replace(
                        "#",
                        ""
                    )
                )
            )

            if (Color.parseColor(modelPhoto.color.toString()).isBrightColor) {
                val color = Color.parseColor("#2E2E2E") //The color u want
                imageViewDownload.setColorFilter(color)
                imageViewWallpaper.setColorFilter(color)
                textViewDownload.setTextColor(color)
                textViewWallpaper.setTextColor(color)

            }

            Glide.with(this@DetailsFragment)
                .load(modelPhoto.urls?.regular?.convertedUrl)
                .error(com.javadEsl.imageSearchApp.R.drawable.ic_error_photos)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        layoutLoading.isVisible = false
                        cardDownload.isVisible = false
                        cardWallpaper.isVisible = false
                        imageView.scaleType = ImageView.ScaleType.CENTER
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        layoutLoading.isVisible = false
                        return false
                    }
                })
                .into(imageView)

            cardWallpaper.setOnClickListener {

                progressBarWallpaper.isVisible = true

                val thread = Thread {
                    val drawable = imageView.drawable
                    val bitmap = drawable.toBitmap()
                    val wallpaperManager =
                        WallpaperManager.getInstance(activity!!.applicationContext)
                    wallpaperManager.setBitmap(bitmap)
                }
                thread.start()

                Handler().postDelayed({
                    progressBarWallpaper.isVisible = false
                    successWallpaperDialog(
                        "Wallpaper applied successfully",
                        activity!!.getDrawable(R.drawable.ic_wallpaper)!!,
                        modelPhoto.color.toString()
                    )
                }, 1500)


            }

            cardDownload.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        startDownloadImage(modelPhoto)
                    }
                } else {
                    startDownloadImage(modelPhoto)
                }
            }
        }
    }

    private fun getUserPhotos(binding: FragmentDetailsBinding) {

        viewModel.liveDataUserPhotosList.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) return@observe
            val adapter = TodoAdapter(it, this)

            binding.apply {
                recViewUserPhotos.setHasFixedSize(true)
                recViewUserPhotos.itemAnimator = null
                recViewUserPhotos.adapter = adapter
            }
        }
    }

    private fun startDownloadImage(modelPhoto: ModelPhoto) {
        initProgressDialog(modelPhoto)

        DownloadConfig.newBuilder()
            .setMaxRunningTaskNum(2)
            .setMinUsableStorageSpace(4 * 1024L)
            .build()

        progressDialog.progress = 0
        progressDialog.show()

//        val filename = "MyApp/Images/" + modelPhoto.id + ".jpg"
//        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename)

        Pump.newRequest(modelPhoto.urls?.full?.convertedUrl)

            .listener(object : com.huxq17.download.core.DownloadListener() {
                override fun onProgress(progress: Int) {
                    progressDialog.progress = progress
                    downloadStatus = true
                }

                override fun onSuccess() {
                    progressDialog.dismiss()
                    downloadStatus = false
                    successWallpaperDialog(
                        "Download was done successfully",
                        activity!!.getDrawable(R.drawable.ic_downward)!!,
                        modelPhoto.color.toString()
                    )
                }

                override fun onFailed() {
                    progressDialog.dismiss()
                    Toast.makeText(
                        activity,
                        "Download Error",
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadStatus = false

                }
            })

            .forceReDownload(true)
            //Optionally,Set how many threads are used when downloading,default 3.
            .threadNum(3)
            .setRetry(3, 200)
            .submit()
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        binding.layoutLoading.isVisible = true
        viewModel.getPhotoDetail(photo.id)
        binding.apply {
            nestedView.smoothScrollTo(0, 0)

        }
    }

    private fun initProgressDialog(modelPhoto: ModelPhoto) {
        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Download " + modelPhoto.id)
        progressDialog.setMessage("Downloading ${modelPhoto.location?.city} ${modelPhoto.id} ...");
        progressDialog.progress = 0
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (downloadStatus) {
            progressDialog.dismiss()
            Pump.shutdown()
        }
    }

    private fun successWallpaperDialog(message: String, drawable: Drawable, color: String) {
        val dialog = Dialog(activity!!, R.style.WallpaperAlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_set_wallapper)

        val cardBackground = dialog.findViewById<CardView>(R.id.card_background_wallpaper_dialog)
        val textViewTitle = dialog.findViewById<TextView>(R.id.text_title_wallpaper_dialog)
        val imageViewIcon = dialog.findViewById<ImageView>(R.id.image_view_icon_wallpaper_dialog)
        val imageViewSuccess =
            dialog.findViewById<ImageView>(R.id.image_view_success_wallpaper_dialog)
        cardBackground.setCardBackgroundColor(
            Color.parseColor(color)
        )
        textViewTitle.text = message
        imageViewIcon.setImageDrawable(drawable)
        if (Color.parseColor(modelPhoto?.color.toString()).isBrightColor) {
            val color = Color.parseColor("#2E2E2E") //The color u want
            imageViewIcon.setColorFilter(color)
            imageViewSuccess.setColorFilter(color)
            textViewTitle.setTextColor(color)

        }else{
            val colorLight = Color.parseColor("#ffffff") //The color u want
            imageViewIcon.setColorFilter(colorLight)
            imageViewSuccess.setColorFilter(colorLight)
            textViewTitle.setTextColor(colorLight)
        }

        Handler().postDelayed({
            dialog.dismiss()
        }, 1500)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

}