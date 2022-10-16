package com.javadEsl.pixel.ui.details

import android.Manifest
import android.app.Dialog
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.huxq17.download.Pump
import com.javadEsl.pixel.*
import com.javadEsl.pixel.api.IMAGE_RAW
import com.javadEsl.pixel.api.IMAGE_REGULAR
import com.javadEsl.pixel.api.IMAGE_SMALL
import com.javadEsl.pixel.data.detail.ModelPhoto
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.data.search.convertedUrl
import com.javadEsl.pixel.databinding.FragmentDetailsBinding
import com.javadEsl.pixel.databinding.LayoutBottomSheetPhotoDetailBinding
import com.javadEsl.pixel.ui.searching.UnsplashPhotoAdapter
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.RenderScriptBlur
import ir.tapsell.plus.AdHolder
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import me.saket.cascade.CascadePopupMenu
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details),
    UnsplashPhotoAdapter.OnItemClickListener, UnsplashUserPhotoAdapter.OnItemClickListener {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var downloadStatus: Boolean = false
    private var isOnSaveClicked = false
    private var isTapsellCreate = false
    var labeler: ImageLabeler? = null
    private var resolutionType = ""
    private var permissionType = ""
    private var responseId = ""
    private var modelPhoto: ModelPhoto? = null
    private lateinit var adHolder: AdHolder
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                modelPhoto?.let { model ->
                    if (checkIsConnection()) {
                        when (permissionType) {
                            "Download" -> {
                                binding.cardDownload.performClick()
                            }
                            "Share" -> {
                                binding.imageView.invalidate()
                                val drawable = binding.imageView.drawable
                                val bitmap = drawable.toBitmap()

                                saveImage(bitmap)?.let { it2 ->
                                    shareImageUri(it2)
                                }
                            }
                            else -> {
                                false
                            }
                        }

                    } else {
                        alertNetworkDialog(requireContext(), model.color.toString())
                    }
                }
            } else {
                showPermissionInfoDialog()
            }
        }
    private val viewModel by viewModels<DetailViewModel>()
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPhotoDetail(args.photoId, args.userName)
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
            if (it == null) return@observe
            setDetail(it)
            modelPhoto = it
        }

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

    private fun setDetail(modelPhoto: ModelPhoto) {
        binding.apply {

            textViewUserName.text = modelPhoto.user?.name.toString()

            if (!modelPhoto.user?.bio.isNullOrEmpty()) {
                textViewBio.isVisible = true
                textViewBio.text = modelPhoto.user?.bio
            }

            if (!modelPhoto.user?.instagramUsername.isNullOrEmpty()) {
                layoutOpenInstagram.show()
            }

            if (modelPhoto.user?.totalPhotos.toString().isNotEmpty()) {
                if (modelPhoto.user?.totalPhotos!! >= 10) {
                    textViewNumberOfPhoto.text = "10 مورد"
                } else {
                    textViewNumberOfPhoto.text = modelPhoto.user.totalPhotos.toString() + " مورد"
                }
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

            if (modelPhoto.location?.name.isNullOrEmpty() && modelPhoto.exif?.model.isNullOrEmpty()) {
                textViewEmptyPhotoInfo.show()
            }

            if (modelPhoto.user?.totalPhotos.toString().isNotEmpty()) {
                textViewProfileImages.text =
                    separationCountNumber(modelPhoto.user?.totalPhotos!!.toInt())
            }
            if (modelPhoto.user?.totalLikes.toString()
                    .isNotEmpty()
            ) {
                if (modelPhoto.user?.totalLikes != 0) {
                    layoutProfileLike.show()
                    textViewProfileLikes.text = separationCountNumber(
                        modelPhoto.user?.totalLikes!!.toInt()
                    )
                }
            }
            if (modelPhoto.views.toString().isNotEmpty()) {
                textViewProfileViews.text =
                    separationCountNumber(modelPhoto.views!!.toInt())
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


            if (modelPhoto.location?.position?.latitude != null) {
                cardViewMap.show()
                cardViewMap.isEnabled = false
                val mapUrl = "https://tile.openstreetmap.org/" + getImage(
                    modelPhoto.location.position.latitude,
                    modelPhoto.location.position.longitude!!,
                    15
                ) + ".png"

                webViewMap.loadUrl(mapUrl)
                webViewMap.setInitialScale(160)
            }

            requireActivity().window.statusBarColor = Color.parseColor(
                "#" + modelPhoto.color?.replace(
                    "#",
                    ""
                )
            )

            if (Color.parseColor(modelPhoto.color.toString()).isBrightColor) {
                val wic = WindowInsetsControllerCompat(
                    requireActivity().window,
                    requireActivity().window.decorView
                )
                wic.isAppearanceLightStatusBars = true
            } else {
                val wic = WindowInsetsControllerCompat(
                    requireActivity().window,
                    requireActivity().window.decorView
                )
                wic.isAppearanceLightStatusBars = false
            }

            lyToolbarDetail.setBackgroundColor(
                Color.parseColor(
                    "#" + modelPhoto.color?.replace(
                        "#",
                        ""
                    )
                )
            )

            viewLineCameraLocation.setBackgroundColor(
                Color.parseColor(
                    "#" + modelPhoto.color?.replace(
                        "#",
                        ""
                    )
                )
            )

            cardViewProfila.strokeColor = Color.parseColor(
                "#" + modelPhoto.color?.replace(
                    "#",
                    ""
                )
            )

            if (Color.parseColor(modelPhoto.color.toString()).isBrightColor) {
                val color = Color.parseColor("#2E2E2E") //The color u want
                imageViewShare.setColorFilter(color)
                imageViewWallpaper.setColorFilter(color)
                imageViewLogoDetail.setColorFilter(color)
                cardLine.setBackgroundColor(color)
                iconBackDetail.setColorFilter(color)
                textViewWallpaper.setTextColor(color)
                textViewShare.setTextColor(color)
                textViewDetailTitle.setTextColor(color)
            }

            layoutDetail.invisible()
            blurViewBackground.invisible()
            Glide.with(this@DetailsFragment)
                .load(modelPhoto.urls?.regular?.convertedUrl)
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
                        showViews()
                        hideViews()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        showViews()
                        layoutLoading.fadeOut()
                        return false
                    }
                })
                .into(imageView)

            layoutOpenInstagram.setOnClickListener {
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
                permissionType = "Share"
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return@setOnClickListener
                }

                imageView.invalidate()
                val drawable = imageView.drawable
                val bitmap = drawable.toBitmap()

                saveImage(bitmap)?.let { it2 ->
                    shareImageUri(it2)
                }
            }

            cardWallpaper.setOnClickListener {

                progressBarWallpaper.isVisible = true
                val thread = Thread {
                    val drawable = imageView.drawable
                    val subBitmap = drawable.toBitmap()
                    val wallpaperManager =
                        WallpaperManager.getInstance(requireActivity().applicationContext)
                    wallpaperManager.setWallpaperOffsetSteps(1f, 1f);
                    wallpaperManager.setBitmap(subBitmap)
                }
                thread.start()

                Handler().postDelayed({
                    progressBarWallpaper.isVisible = false
                    successDialog(
                        getString(R.string.string_alert_success_wallpaper),
                        requireActivity().getDrawable(R.drawable.ic_wallpaper)!!,
                        modelPhoto.color.toString(),
                        1500
                    )
                }, 1500)
            }

            cardDownload.setOnClickListener {
                permissionType = "Download"
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    return@setOnClickListener
                }
                DownloaderService()
                showDownloadMenu(anchor = it)
            }

            cardViewBackToolbarDetail.setOnClickListener {
                findNavController().popBackStack()
            }

            imageView.setOnClickListener {
                val drawable = imageView.drawable
                val bitmap = drawable?.toBitmap()
                if (bitmap != null) {
                    showImageSheetDialog(bitmap)
                }
            }

            viewOnMapClick.setOnClickListener {
                val latitude = modelPhoto.location?.position?.latitude
                val longitude = modelPhoto.location?.position?.longitude
                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(requireContext().packageManager)?.let {
                    startActivity(mapIntent)
                }
            }

        }
    }

    private fun showAd(adHolder: AdHolder) {

        TapsellPlus.showNativeAd(requireActivity(), responseId, adHolder,
            object : AdShowListener() {
                override fun onOpened(tapsellPlusAdModel: TapsellPlusAdModel) {
                    super.onOpened(tapsellPlusAdModel)
                    isTapsellCreate = true
                    Log.d("ContentValues", "Ad Open")
                }

                override fun onError(tapsellPlusErrorModel: TapsellPlusErrorModel) {
                    super.onError(tapsellPlusErrorModel)
                    Log.e("onError", tapsellPlusErrorModel.toString())
                    isTapsellCreate = false
                }
            })
    }

    private fun destroyAd() {
        if (isTapsellCreate)
            TapsellPlus.destroyNativeBanner(requireActivity(), responseId)
    }

    private fun showDownloadMenu(anchor: View) {
        val popupMenu = CascadePopupMenu(requireContext(), anchor, styler = cascadeMenuStyler())
        popupMenu.menu.apply {
            MenuCompat.setGroupDividerEnabled(this, false)
            // setHeaderTitle("Are you sure?")
            modelPhoto?.urls?.small?.let {
                add(IMAGE_SMALL)
            }
            modelPhoto?.urls?.regular?.let {
                add(IMAGE_REGULAR)
            }
            modelPhoto?.urls?.raw?.let {
                add(IMAGE_RAW)
            }
        }
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->


            resolutionType = menuItem.toString()
            if (checkIsConnection()) {

                isOnSaveClicked = true
                var resolutionTypeSelected = ""
                when (resolutionType) {
                    IMAGE_REGULAR -> {
                        resolutionTypeSelected = "HD"
                    }
                    IMAGE_RAW -> {
                        resolutionTypeSelected = "Full-HD"
                    }
                    IMAGE_SMALL -> {
                        resolutionTypeSelected = "SD"
                    }
                }
                val root = Environment.getExternalStorageDirectory()
                val myDir =
                    File("${root}/${getString(R.string.app_name)}/${modelPhoto?.id}/${resolutionTypeSelected}.jpg")

                if (!myDir.exists()) {
                    modelPhoto?.let { it1 -> downloadPhoto(it1) }
                    popupMenu.dismiss()
                } else {

                    alert(getString(R.string.string_alert_exist_photo))
                }
            } else {
                alertNetworkDialog(requireContext(), modelPhoto?.color.toString())
                popupMenu.dismiss()
            }
            popupMenu.navigateBack()

        }

    }

    private fun cascadeMenuStyler(): CascadePopupMenu.Styler {
        val rippleDrawable = {
            RippleDrawable(
                ColorStateList.valueOf(Color.parseColor(getString(R.color.color_download_popup))),
                null,
                ColorDrawable(WHITE)
            )
        }

        return CascadePopupMenu.Styler(
            background = {
                RoundedRectDrawable(
                    ResourcesCompat.getColor(resources, R.color.color_download_popup, null),
                    radius = 15f.dip
                )
            },
            menuTitle = {
                it.setBackground(rippleDrawable())
            },
            menuItem = {
                it.titleView.textSize = 14f
//                it.titleView.typeface = ResourcesCompat.getFont(requireContext(), R.font.work_sans_medium)
                it.setBackground(rippleDrawable())
                it.setGroupDividerColor(Color.parseColor(getString(R.color.color_download_popup)))
            }
        )
    }

    private val Float.dip: Float
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)
        }

    private fun addWatermark(source: Bitmap): Bitmap? {
        val canvas: Canvas
        val bitmap: Bitmap
        val scale: Float
        val rectF: RectF
        val width: Int = source.width
        val height: Int = source.height

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val paint: Paint =
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas = Canvas(bitmap)
        canvas.drawBitmap(source, 0f, 0f, paint)

        val watermark: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_splash)

        scale = ((200.toFloat() * 0.43f) / watermark.height)
        val matrix: Matrix = Matrix()
        matrix.postScale(scale, scale)
        rectF = RectF(0f, 0f, watermark.width.toFloat(), watermark.height.toFloat())
        matrix.mapRect(rectF)

        matrix.postTranslate(20f, height.toFloat() - (watermark.height))

        canvas.drawBitmap(watermark, matrix, paint)
        watermark.recycle()

        return bitmap
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
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.message)
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

    private fun downloadPhoto(modelPhoto: ModelPhoto) {

        if (isRequireExternalStorageManager()) {
            requestPermission()
            return
        }

        var downloadLink = "https://"
        var typeFile = "SD"
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
            "${root}/${getString(R.string.app_name)}/${modelPhoto.id}/${typeFile}.jpg"
        )
        myDir.mkdirs()

        if (!myDir.exists()) {
            alert(getString(R.string.string_alert_permission))
            return
        }

        DownloaderService.start(requireContext(), downloadLink, myDir.path)

        successDialog(
            getString(R.string.string_alert_start_download),
            requireContext().getDrawable(R.drawable.ic_cloud_download)!!,
            modelPhoto.color.toString(),
            1000
        )
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
        val dialog = Dialog(requireActivity(), R.style.AlertDialog)
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

    private fun showPermissionInfoDialog() {
        val dialog = Dialog(requireActivity(), R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_dialog_permissiont_info)


        val btnSetting = dialog.findViewById<Button>(R.id.btn_setting)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)

        btnSetting.setOnClickListener {
            requireContext().openAppSystemSettings()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

    private fun Context.openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
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

    override fun onItemClick(photo: PixelPhoto) {
        if (checkIsConnection()) {
            binding.layoutLoading.fadeIn()
            binding.layoutDetail.fadeOut()
            binding.lyClassifications.fadeOut()
            viewModel.getPhotoDetail(photo.id, photo.user?.username.toString())
            binding.apply {
                nestedView.smoothScrollTo(0, 0)

            }
        } else {
            alertNetworkDialog(requireContext(), modelPhoto?.color.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyAd()
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

    private fun showViews() = binding.apply {
        val radius = 10f
        val decorView: View = requireActivity().window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content) as ViewGroup

        val windowBackground = decorView.background

        blurViewBackground.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)

        blurViewMap.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(5f)

        blurViewBackground.expand(duration = 1000)
        layoutDetail.fadeIn(duration = 1000)

        adHolder = TapsellPlus.createAdHolder(
            requireActivity(),
            binding.adContainer,
            R.layout.native_banner_detail
        )!!

        TapsellPlus.requestNativeAd(
            requireActivity(),
            BuildConfig.TAPSELL_NATIVE_BANNER,
            object : AdRequestCallback() {
                override fun response(tapsellPlusAdModel: TapsellPlusAdModel) {
                    super.response(tapsellPlusAdModel)
                    responseId = tapsellPlusAdModel.responseId
                    showAd(adHolder)
                }

                override fun error(message: String) {
                    Log.e(TAG, "error: $message")
                }
            })

        Handler(Looper.getMainLooper()).postDelayed({

            val options = ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
            labeler = ImageLabeling.getClient(options)

            val drawable = imageView.drawable

            if (drawable != null) {
                val bitmap = drawable.toBitmap()
                runClassification(bitmap)
            }

        }, 1500)

    }

    private fun hideViews() = binding.apply {
        layoutLoading.hide()
        cardDownload.hide()
        lyDetailImage.hide()
        lyToolbarDetail.hide()
        cardShare.hide()
        cardWallpaper.hide()
        lyClassifications.hide()
    }

    private fun getImage(lat: Double, lon: Double, zoom: Int): String {
        var xtile = Math.floor((lon + 180) / 360 * (1 shl zoom)).toInt()
        var ytile =
            Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 shl zoom))
                .toInt()
        if (xtile < 0) xtile = 0
        if (xtile >= 1 shl zoom) xtile = (1 shl zoom) - 1
        if (ytile < 0) ytile = 0
        if (ytile >= 1 shl zoom) ytile = (1 shl zoom) - 1
        return "" + zoom.toString() + "/" + xtile.toString() + "/" + ytile
    }

    private fun showImageSheetDialog(photo: Bitmap) {
        val dialog = BottomSheetDialog(
            requireContext(), R.style.AppBottomSheetDialogTheme
        )

        val sheetDialog =
            LayoutBottomSheetPhotoDetailBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(sheetDialog.root)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.isDraggable = false

        sheetDialog.apply {

            Glide.with(requireContext())
                .load(photo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(photoView)

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

                saveImage(photo)?.let { Uri ->
                    shareImageUri(Uri)
                }
            }
            cardViewBackToolbarBottomSheet.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun separationCountNumber(number: Int): String {
        var value = ""
        when (number) {
            in 1_000_000..9_999_999 -> {
                value = number.toString().substring(0, 1) + "+" + " میلیون"
            }
            in 10_000_000..99_999_999 -> {
                value = number.toString().substring(0, 2) + "+" + " میلیون"
            }
            in 100_000_000..999_999_999 -> {
                value = number.toString().substring(0, 3) + "+" + " میلیون"
            }
            in 1..999_999 -> {
                value = number.toDecimal()
            }
            else -> ""
        }

        return value
    }

    private fun runClassification(bitmap: Bitmap) {
        val inputImage: InputImage = InputImage.fromBitmap(bitmap, 0)
        labeler?.process(inputImage)?.addOnSuccessListener { labels ->
            if (labels.size > 0) {
                val list = labels.map { it.text }
                val count = if (labels.size in 6..9) 2 else if (labels.size in 9..19) 3 else 1

                val layoutManager = StaggeredGridLayoutManager(count, RecyclerView.HORIZONTAL)
                val adapter = ClassificationAdapter(list)
                binding.recClassification.apply {
                    this.adapter = adapter
                    this.layoutManager = layoutManager
                }
                binding.lyClassifications.fadeIn()
            } else {
                binding.lyClassifications.fadeOut()
            }
        }?.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

}
