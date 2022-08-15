package com.javadEsl.imageSearchApp.ui.gallery

import androidx.lifecycle.ViewModel
import com.javadEsl.imageSearchApp.data.UnsplashRepository
import javax.inject.Inject

class GalleryViewModel @Inject constructor(private val unsplashRepository: UnsplashRepository) : ViewModel() {
}