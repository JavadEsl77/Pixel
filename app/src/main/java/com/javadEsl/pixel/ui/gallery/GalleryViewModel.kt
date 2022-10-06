package com.javadEsl.pixel.ui.gallery

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.topics.TopicsModelItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    val allPhotos = pixelRepository.getAllPhotos().cachedIn(viewModelScope)

    private val _liveDataTopics = MutableLiveData<List<TopicsModelItem>>()
    val liveDataTopics: LiveData<List<TopicsModelItem>> = _liveDataTopics
    fun topicsList() {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataTopics.postValue(emptyList())
                return@launch
            }

            try {
                val response = pixelRepository.getTopics()
                val photos = response.toMutableList()
                if (photos.isNotEmpty()) {
                    photos.add(0,TopicsModelItem(id = "user_type" , title = "recommended for you"))
                }
                if (response.isNotEmpty()) {
                    _liveDataTopics.postValue(photos)
                }
            } catch (e: Exception) {
                Log.e("TAG", "getAutocomplete: $e")
            }

        }
    }

}