package com.javadEsl.pixel.ui.splash

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.javadEsl.pixel.BuildConfig
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.FragmentSplashBinding
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
        binding.apply {

            viewModel.connection.observe(viewLifecycleOwner) {
                stateNetwork = it!!
                if (it == false) {
                    layoutError.isVisible = true
                    layoutLoading.isVisible = false
                    animationView.isVisible = false
                    return@observe
                } else {
                    animationView.isVisible = true
                    layoutLoading.isVisible = true
                    layoutError.isVisible = false
                    Handler().postDelayed({
                        val action =
                            SplashFragmentDirections.actionSplashFragmentToGalleryFragment()
                        findNavController().navigate(action)
                    }, 3000)
                }
            }

            val versionCode: Int = BuildConfig.VERSION_CODE
            val versionName: String = BuildConfig.VERSION_NAME
            textViewVersion.text = " نسخه $versionName"

            textViewRetry.setOnClickListener {
                viewModel.getStartNavigate()
                if (!stateNetwork) {
                    val anim: Animation = AlphaAnimation(0.5f, 1.0f)
                    anim.duration = 250 //You can manage the blinking time with this parameter

                    anim.startOffset = 50
                    anim.repeatMode = Animation.REVERSE
                    anim.repeatCount = Animation.INFINITE
                    textViewNetworkError.startAnimation(anim)

                    Handler().postDelayed({
                        anim.cancel()
                    }, 1000)
                }



            }
        }
    }
}