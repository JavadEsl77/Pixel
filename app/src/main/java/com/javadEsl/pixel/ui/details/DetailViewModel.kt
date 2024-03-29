package com.javadEsl.pixel.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadEsl.pixel.helper.NetworkHelper
import com.javadEsl.pixel.data.repository.PixelRepository
import com.javadEsl.pixel.data.model.detail.ModelPhoto
import com.javadEsl.pixel.data.model.search.PixelPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _liveDataList = MutableLiveData<ModelPhoto?>()
    val liveDataList: LiveData<ModelPhoto?> = _liveDataList
    private val _liveDataUserPhotosList = MutableLiveData<List<PixelPhoto>?>()
    val liveDataUserPhotosList: LiveData<List<PixelPhoto>?> = _liveDataUserPhotosList
    private var photoResponse: ModelPhoto? = null

    fun getPhotoDetail(photoId:String , userName: String) {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataList.postValue(null)
                return@launch
            }
            try {
                val response = pixelRepository.getPhotoDetail(photoId.toString())
                if (response.isSuccessful) {
                    photoResponse = response.body()
                    getUserPhotos(userName)
                }
            } catch (e: Exception) {
                _liveDataList.postValue(null)
            }
        }
    }

    private fun getUserPhotos(userName: String) {
        viewModelScope.launch {
            try {
                val response = pixelRepository.getUserPhotos(userName)
                if (!response.isNullOrEmpty()) {
                    _liveDataList.postValue(photoResponse)
                    _liveDataUserPhotosList.postValue(response)
                }
            } catch (e: Exception) {

            }
        }
    }
}