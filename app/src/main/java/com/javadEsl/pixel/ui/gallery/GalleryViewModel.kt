package com.javadEsl.pixel.ui.gallery

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.autocomplete.Suggestion
import com.javadEsl.pixel.data.detail.ModelPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    val allPhotos = pixelRepository.getAllPhotos().cachedIn(viewModelScope)
}