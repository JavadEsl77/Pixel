package com.javadEsl.pixel.ui.splash

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.javadEsl.pixel.BuildConfig
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.FragmentSplashBinding
import com.javadEsl.pixel.slideUp
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
        requireActivity().window.statusBarColor = Color.parseColor(
            "#" + "000000".replace(
                "#",
                ""
            )
        )
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        binding.apply {

            animationView.slideUp(1000,0)

            viewModel.connection.observe(viewLifecycleOwner) {
                stateNetwork = it == true
                if (it == false) {
                    layoutError.slideUp(1000,0)
                    layoutError.isVisible = true
                    layoutLoading.isVisible = false
                    animationView.isVisible = false
                    return@observe
                } else {
                    animationView.slideUp(1000,0)
                    animationView.isVisible = true
                    layoutLoading.isVisible = true
                    layoutError.isVisible = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        val action = SplashFragmentDirections.actionSplashFragmentToGalleryFragment()
                        findNavController().navigateUp()
                        findNavController().navigate(action)


                    }, 3000)
                }
            }

            val versionName: String = BuildConfig.VERSION_NAME
            textViewVersion.text = "نسخه   $versionName"

            textViewRetry.setOnClickListener {
                viewModel.getStartNavigate()
                if (!stateNetwork) {
                    val anim: Animation = AlphaAnimation(0.5f, 1.0f)
                    anim.duration = 250 //You can manage the blinking time with this parameter

                    anim.startOffset = 50
                    anim.repeatMode = Animation.REVERSE
                    anim.repeatCount = Animation.INFINITE
                    textViewNetworkError.startAnimation(anim)

                    Handler(Looper.getMainLooper()).postDelayed({
                        anim.cancel()
                    }, 1000)
                }


            }
        }
    }


    private fun showPermissionInfoDialog() {
        val dialog = Dialog(requireActivity(), R.style.AlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_dialog_permissiont_info)

        val btnSetting = dialog.findViewById<Button>(R.id.btn_setting)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)

        btnSetting.setOnClickListener {
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

}
