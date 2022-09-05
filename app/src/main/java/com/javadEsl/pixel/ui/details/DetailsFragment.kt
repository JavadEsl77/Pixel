package com.javadEsl.pixel.ui.details

import android.Manifest
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.huxq17.download.Pump
import com.huxq17.download.config.DownloadConfig
import com.javadEsl.CANCEL_DOWNLOAD_MENU
import com.javadEsl.IMAGE_RAW
import com.javadEsl.IMAGE_REGULAR
import com.javadEsl.IMAGE_SMALL
import com.javadEsl.pixel.*
import com.javadEsl.pixel.data.ModelPhoto
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.convertedUrl
import com.javadEsl.pixel.databinding.FragmentDetailsBinding
import com.javadEsl.pixel.ui.gallery.UnsplashPhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.saket.cascade.CascadePopupMenu
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details),
    UnsplashPhotoAdapter.OnItemClickListener, UnsplashUserPhotoAdapter.OnItemClickListener {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var downloadStatus: Boolean = false
    private var isOnSaveClicked = false
    private var resolutionType = ""
    private var modelPhoto: ModelPhoto? = null
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                modelPhoto?.let { model ->

                    if (checkIsConnection()) {

                        val root = Environment.getExternalStorageDirectory()
                        val myDir = File("${root}/Pixel/${model.id}.jpg")

                        if (!myDir.exists()) {
                            downloadDialog(model, false)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "این تصویر در حافظه موجود می باشد",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        alertNetworkDialog(requireContext(), model.color.toString())
                    }

                }
            }
        }

    @Inject
    lateinit var photo: PixelRepository
    private val viewModel by viewModels<DetailViewModel>()
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasInternetConnection {
            if (it) {
                viewModel.getPhotoDetail(args.photo.id)
                viewModel.getUserPhotos(args.photo.user?.username.toString())
            }

        }
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
        initViewModel()
    }

    private fun initViewModel() {

        viewModel.liveDataList.observe(viewLifecycleOwner) {

            if (it == null)
                return@observe

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
                textViewCameraModel.text =
                    modelPhoto.exif?.make + " , " + modelPhoto.exif?.model
            }

            if (modelPhoto.views.toString().isNotEmpty()) {
                textViewTitlePhotoInfo.isVisible = true
                layoutViewInfo.isVisible = true
                textViewPhotoViews.isVisible = true
                textViewPhotoViews.text = modelPhoto.views.toDecimal()
            }

            if (modelPhoto.downloads.toString().isNotEmpty()) {
                textViewTitlePhotoInfo.isVisible = true
                layoutDownloadInfo.isVisible = true
                textViewPhotoDownload.isVisible = true
                textViewPhotoDownload.text = modelPhoto.downloads.toDecimal()
            }

            if (modelPhoto.likes.toString().isNotEmpty()) {
                layoutLikeInfo.isVisible = true
                textViewPhotoLikes.isVisible = true
                textViewPhotoLikes.text = modelPhoto.likes.toDecimal()
            }

            if (modelPhoto.location?.name.isNullOrEmpty() || modelPhoto.exif?.model.isNullOrEmpty()) {
                viewLineCameraLocation.isVisible = false
            }

            Glide.with(this@DetailsFragment)
                .load(modelPhoto.user?.profileImage?.large?.convertedUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_user)
                .into(imageViewProfile)
            cardDownload.setCardBackgroundColor(
                Color.parseColor(
                    "#cc" + modelPhoto.color?.replace(
                        "#",
                        ""
                    )
                )
            )
            cardShare.setCardBackgroundColor(
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
                imageViewShare.setColorFilter(color)
                imageViewWallpaper.setColorFilter(color)
                textViewDownload.setTextColor(color)
                textViewWallpaper.setTextColor(color)
                textViewShare.setTextColor(color)
            }
            Glide.with(this@DetailsFragment)
                .load(modelPhoto.urls?.small?.convertedUrl)
                .error(R.drawable.ic_error_photos)
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
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        layoutLoading.isVisible = false
                        return false
                    }
                })
                .into(imageView)

            textViewInstagram.setOnClickListener {
                val uri: Uri =
                    Uri.parse("http://instagram.com/${modelPhoto.user?.instagramUsername}")
                val likeIng = Intent(Intent.ACTION_VIEW, uri)
                likeIng.setPackage("com.instagram.android")

                try {
                    startActivity(likeIng)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/${modelPhoto.user?.instagramUsername}")
                        )
                    )
                }
            }

            cardShare.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return@setOnClickListener
                }

                if (checkIsConnection()) {
                    resolutionType = IMAGE_REGULAR
                    isOnSaveClicked = true
                    val root = Environment.getExternalStorageDirectory()
                    val myDir = File("${root}/Pixel/${modelPhoto.id}/HD.jpg")

                    if (!myDir.exists()) {
                        downloadDialog(modelPhoto, true)

                    } else {
                        shareImage(modelPhoto)
                    }
                } else {
                    alertNetworkDialog(requireContext(), modelPhoto.color.toString())
                }

            }

            cardWallpaper.setOnClickListener {

                progressBarWallpaper.isVisible = true
                val thread = Thread {
                    val drawable = imageView.drawable
                    val subBitmap = drawable.toBitmap()
                    val wallpaperManager =
                        WallpaperManager.getInstance(activity!!.applicationContext)
                    wallpaperManager.setWallpaperOffsetSteps(1f, 1f);
                    wallpaperManager.setBitmap(subBitmap)
                }
                thread.start()

                Handler().postDelayed({
                    progressBarWallpaper.isVisible = false
                    successDialog(
                        getString(R.string.string_alert_success_wallpaper),
                        activity!!.getDrawable(R.drawable.ic_wallpaper)!!,
                        modelPhoto.color.toString(),
                        1500
                    )
                }, 1500)
            }

            cardDownload.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return@setOnClickListener
                }
                showDownloadMenu(anchor = it)
            }
        }
    }

    private fun getUserPhotos(binding: FragmentDetailsBinding) {

        viewModel.liveDataUserPhotosList.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) return@observe

            val adapter = UnsplashUserPhotoAdapter(it, this)
            binding.apply {
                recViewUserPhotos.setHasFixedSize(true)
                recViewUserPhotos.itemAnimator = null
                recViewUserPhotos.adapter = adapter
            }
        }
    }

    private fun showDownloadMenu(anchor: View) {
        val popupMenu = CascadePopupMenu(requireContext(), anchor, styler = cascadeMenuStyler())
        popupMenu.menu.apply {
            MenuCompat.setGroupDividerEnabled(this, true)

            modelPhoto?.urls?.small?.let {
                add(IMAGE_SMALL)
            }
            modelPhoto?.urls?.regular?.let {
                add(IMAGE_REGULAR)
            }
            modelPhoto?.urls?.raw?.let {
                add(IMAGE_RAW)
            }
            add(CANCEL_DOWNLOAD_MENU)
        }
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            if (menuItem.toString() != CANCEL_DOWNLOAD_MENU) {
                resolutionType = menuItem.toString()
                if (checkIsConnection()) {
                    isOnSaveClicked = true
                    var resolutionTypeSelected = ""
                    when (resolutionType) {
                        IMAGE_REGULAR -> {
                            resolutionTypeSelected = "HD"
                        }
                        IMAGE_RAW     -> {
                            resolutionTypeSelected = "Full-HD"
                        }
                        IMAGE_SMALL   -> {
                            resolutionTypeSelected = "SD"
                        }
                    }
                    val root = Environment.getExternalStorageDirectory()
                    val myDir =
                        File("${root}/Pixel/${modelPhoto?.id}/${resolutionTypeSelected}.jpg")

                    if (!myDir.exists()) {
                        modelPhoto?.let { it1 -> downloadDialog(it1, false) }
                        popupMenu.dismiss()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "این تصویر در حافظه موجود می باشد",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    alertNetworkDialog(requireContext(), modelPhoto?.color.toString())
                    popupMenu.dismiss()
                }
            } else {
                popupMenu.dismiss()
            }
            popupMenu.navigateBack()

        }

    }

    private fun cascadeMenuStyler(): CascadePopupMenu.Styler {
        val rippleDrawable = {
            RippleDrawable(
                ColorStateList.valueOf(Color.parseColor("#b1cadd")),
                null,
                ColorDrawable(WHITE)
            )
        }

        return CascadePopupMenu.Styler(
            background = {
                RoundedRectDrawable(
                    requireContext().resources.getColor(R.color.color_menu_download),
                    radius = 10f.dip
                )
            },
            menuTitle = {
                it.setBackground(rippleDrawable())
            },
            menuItem = {
//                it.titleView.typeface = ResourcesCompat.getFont(requireContext(), R.font.work_sans_medium)
                it.setBackground(rippleDrawable())
                it.setGroupDividerColor(Color.parseColor("#becfd9"))
            }
        )
    }

    private val Float.dip: Float
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)
        }

    private fun shareImage(modelPhoto: ModelPhoto) {
        try {

            val root = Environment.getExternalStorageDirectory()
            val myFile = File("${root}/Pixel/${modelPhoto.id}/HD.jpg")

            val imageUri = FileProvider.getUriForFile(
                requireContext(), "com.example.homefolder.example.provider", myFile
            )
            val sharingIntent = Intent("android.intent.action.SEND")
            sharingIntent.type = "*/*"
            sharingIntent.putExtra("android.intent.extra.STREAM", imageUri)
            startActivity(Intent.createChooser(sharingIntent, "Share using"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadDialog(modelPhoto: ModelPhoto, shareAfter: Boolean) {

        if (isRequireExternalStorageManager()) {
            requestPermission()
            return
        }
        var downloadLink = "https://"
        var typeFile = "SD"
        val dialog = Dialog(activity!!, R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_downlaod_dialog)

        val processBarDownload = dialog.findViewById<ProgressBar>(R.id.progress_bar_download)
        val textViewTitleFile = dialog.findViewById<TextView>(R.id.text_view_title_file)
        textViewTitleFile.text = modelPhoto.id + ".jpg"

        DownloadConfig.newBuilder()
            .setMaxRunningTaskNum(2)
            .setMinUsableStorageSpace(4 * 1024L)
            .build()

        when (resolutionType) {
            IMAGE_SMALL -> {
                downloadLink = modelPhoto.urls?.small?.convertedUrl.toString()
                typeFile = "SD"
            }
            IMAGE_REGULAR -> {
                downloadLink = modelPhoto.urls?.regular?.convertedUrl.toString()
                typeFile = "HD"
            }
            IMAGE_RAW -> {
                downloadLink = modelPhoto.urls?.raw?.convertedUrl.toString()
                typeFile = "Full-HD"
            }
            "" -> {
                downloadLink = modelPhoto.urls?.regular?.convertedUrl.toString()
                typeFile = "HD"
            }
        }

        val root = Environment.getExternalStorageDirectory()
        val myDir = File(
            "${root}/Pixel/${modelPhoto.id}/${typeFile}.jpg"
        )
        myDir.mkdirs()

        if (!myDir.exists()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.string_alert_permission),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Pump.newRequest(downloadLink, myDir.path)
            .listener(object : com.huxq17.download.core.DownloadListener() {
                override fun onProgress(progress: Int) {
                    processBarDownload.progress = progress
                    downloadStatus = true
                }

                override fun onSuccess() {
                    dialog.dismiss()
                    downloadStatus = false
                    isOnSaveClicked = false
                    successDialog(
                        getString(R.string.string_alert_success_download),
                        activity!!.getDrawable(R.drawable.ic_downward)!!,
                        modelPhoto.color.toString(),
                        1500
                    )
                    if (shareAfter) {
                        shareImage(modelPhoto)
                    }
                }

                override fun onFailed() {
                    dialog.dismiss()
                    Toast.makeText(
                        activity,
                        "Download Error",
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadStatus = false
                    isOnSaveClicked = false

                }
            })
            .forceReDownload(true)
            .threadNum(3)
            .setRetry(3, 200)
            .submit()
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

    private fun alertNetworkDialog(context: Context, color: String) {
        val dialog = Dialog(context, R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_network_alert)

        val cardBackground = dialog.findViewById<CardView>(R.id.card_background_network_dialog)
        val textViewTitle = dialog.findViewById<TextView>(R.id.text_title_network_dialog)
        val imageViewIcon = dialog.findViewById<ImageView>(R.id.image_view_icon_network_dialog)

        cardBackground.setCardBackgroundColor(
            Color.parseColor(color)
        )

        if (Color.parseColor(modelPhoto?.color.toString()).isBrightColor) {
            val color = Color.parseColor("#2E2E2E") //The color u want
            imageViewIcon.setColorFilter(color)
            textViewTitle.setTextColor(color)

        } else {
            val colorLight = Color.parseColor("#ffffff") //The color u want
            imageViewIcon.setColorFilter(colorLight)
            textViewTitle.setTextColor(colorLight)
        }

        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)

        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    private fun successDialog(
        message: String,
        drawable: Drawable,
        color: String,
        duration: Long
    ) {
        val dialog = Dialog(activity!!, R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_success)

        val cardBackground = dialog.findViewById<CardView>(R.id.card_background_success_dialog)
        val textViewTitle = dialog.findViewById<TextView>(R.id.text_title_wallpaper_dialog)
        val imageViewIcon = dialog.findViewById<ImageView>(R.id.image_view_icon_success_dialog)
        val imageViewSuccess =
            dialog.findViewById<ImageView>(R.id.image_view_success_dialog)
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

        } else {
            val colorLight = Color.parseColor("#ffffff") //The color u want
            imageViewIcon.setColorFilter(colorLight)
            imageViewSuccess.setColorFilter(colorLight)
            textViewTitle.setTextColor(colorLight)
        }

        Handler().postDelayed({
            dialog.dismiss()
        }, duration)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

    private fun checkIsConnection(): Boolean {
        val connectionManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection =
            connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (return wifiConnection?.isConnectedOrConnecting == true || (mobileDataConnection?.isConnectedOrConnecting == true)) true
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

    fun hasInternetConnection(
        url: String = "https://www.google.com/",
        callback: (Boolean) -> Unit
    ) {
        CoroutineScope(context = Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder()
                    .url(URL(url))
                    .get()
                    .build()
                client.newCall(request).execute()

                callback.invoke(true)
                requireContext().sendBroadcast(Intent().apply {
                    action = NetworkHelper.CONNECTED_ACTION
                })
            } catch (e: IOException) {
                callback.invoke(false)
                requireContext().sendBroadcast(Intent().apply {
                    action = NetworkHelper.DISCONNECTED_ACTION
                })
            }
        }
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        if (checkIsConnection()) {
            binding.layoutLoading.isVisible = true
            viewModel.getPhotoDetail(photo.id)
            binding.apply {
                nestedView.smoothScrollTo(0, 0)

            }
        } else {
            alertNetworkDialog(requireContext(), modelPhoto?.color.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (downloadStatus) {
            Pump.shutdown()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            Environment.isExternalStorageManager() &&
            isOnSaveClicked
        ) {
            binding.cardDownload.performClick()
        }
    }
}