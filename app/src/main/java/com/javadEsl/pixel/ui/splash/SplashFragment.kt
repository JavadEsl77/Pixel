package com.javadEsl.pixel.ui.splash

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.javadEsl.pixel.*
import com.javadEsl.pixel.data.model.update.Update
import com.javadEsl.pixel.databinding.FragmentSplashBinding
import com.javadEsl.pixel.databinding.LayoutDialogUpdateBinding
import com.javadEsl.pixel.helper.Resource
import com.javadEsl.pixel.helper.extensions.browseUrl
import com.javadEsl.pixel.helper.extensions.isPackageInstalled
import com.javadEsl.pixel.helper.extensions.show
import com.javadEsl.pixel.helper.extensions.slideUp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var stateNetwork = false
    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStartNavigate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        setupViews()
        observe()
    }

    private fun setupViews() = binding.apply {
        requireActivity().window.statusBarColor = Color.parseColor(
            "#" + "000000".replace(
                "#",
                ""
            )
        )
        val wic = WindowInsetsControllerCompat(
            requireActivity().window,
            requireActivity().window.decorView
        )
        wic.isAppearanceLightStatusBars = false

        animationView.slideUp(1000, 0)
        val versionName: String = BuildConfig.VERSION_NAME
        textViewVersion.text = "نسخه   $versionName"

        textViewRetry.setOnClickListener {
            animationView.slideUp(1000, 0)
            viewModel.getStartNavigate()
        }
    }

    private fun observe() = binding.apply {
        viewModel.connection.observe(viewLifecycleOwner) {
            stateNetwork = it == true
            if (it == false) {
                layoutError.slideUp(1000, 0)
                layoutError.isVisible = true
                layoutLoading.isVisible = false
                animationView.isVisible = false
                return@observe
            } else {
                animationView.slideUp(1000, 0)
                animationView.isVisible = true
                layoutError.isVisible = false
                layoutError.isVisible = false
                viewModel.checkUpdate()
            }
        }

        viewModel.checkUpdate.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data.versionCode!! > BuildConfig.VERSION_CODE) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            showUpdateDialog(it.data)
                        }, 1500)
                    } else {

//                        animationView.slideUp(1000, 0)
//                        animationView.isVisible = true
//                        layoutLoading.isVisible = true
//                        layoutError.isVisible = false

                        Handler(Looper.getMainLooper()).postDelayed({
                            val action =
                                SplashFragmentDirections.actionSplashFragmentToGalleryFragment()
                            findNavController().navigateUp()
                            findNavController().navigate(action)
                        }, 1500)
                    }
                }
                is Resource.Failure -> {
                    layoutError.slideUp(1000, 0)
                    layoutError.isVisible = true
                    layoutLoading.isVisible = false
                    animationView.isVisible = false
                }
                else                -> Unit
            }
        }

    }

    private fun showUpdateDialog(apiResponse: Update) {
        val dialog = Dialog(requireActivity(), R.style.AlertDialog)
        val updateDialog = LayoutDialogUpdateBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(updateDialog.root)

        updateDialog.apply {
            textViewUpdateInfo.text = apiResponse.message
            btnUpdate.text = apiResponse.links?.get(0)?.text.toString()
            btnCancel.text = apiResponse.ignoreButtonText.toString()
            if (apiResponse.isForce == true) {
                textViewUpdateForce.show()
            }

            btnCancel.setOnClickListener {
                if (apiResponse.isForce == true) {
                    requireActivity().finish()
                } else {
                    dialog.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val action =
                            SplashFragmentDirections.actionSplashFragmentToGalleryFragment()
                        findNavController().navigateUp()
                        findNavController().navigate(action)
                    }, 1500)
                }
            }
            btnUpdate.setOnClickListener {
                val isInstalled = isPackageInstalled("com.farsitel.bazaar")
                if (isInstalled) {
                    browseUrl(apiResponse.links?.get(0)?.url)
                    return@setOnClickListener
                }
                browseUrl("https://cafebazaar.ir/app/com.javadEsl.pixel")
            }
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

}
