package com.javadEsl.pixel.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.data.ModelPhoto
import com.javadEsl.pixel.data.UnsplashPhoto
import com.javadEsl.pixel.data.PixelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _liveDataList = MutableLiveData<ModelPhoto?>()
    val liveDataList: LiveData<ModelPhoto?> = _liveDataList

    private val _liveDataUserPhotosList = MutableLiveData<List<UnsplashPhoto>?>()
    val liveDataUserPhotosList: LiveData<List<UnsplashPhoto>?> = _liveDataUserPhotosList

    fun getPhotoDetail(id: String) {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataList.postValue(null)
                return@launch
            }
            try {
                val response = pixelRepository.getPhotoDetail(id)
                if (response.isSuccessful) {
                    _liveDataList.postValue(response.body())
                }
            } catch (e: Exception) {
                _liveDataList.postValue(null)
            }

        }
    }

    fun getUserPhotos(userName: String) {
        viewModelScope.launch {
            try {
                val response = pixelRepository.getUserPhotos(userName)
                if (!response.isNullOrEmpty()) {
                    _liveDataUserPhotosList.postValue(response)
                }
            } catch (e: Exception) {

            }
        }
    }
}