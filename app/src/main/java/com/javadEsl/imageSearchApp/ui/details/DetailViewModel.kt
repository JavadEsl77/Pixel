package com.javadEsl.imageSearchApp.ui.details

import androidx.lifecycle.*
import com.javadEsl.imageSearchApp.data.ModelPhoto
import com.javadEsl.imageSearchApp.data.UnsplashPhoto
import com.javadEsl.imageSearchApp.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val unsplashRepository: UnsplashRepository
) : ViewModel() {

    private val _liveDataList = MutableLiveData<ModelPhoto>()
    val liveDataList: LiveData<ModelPhoto> = _liveDataList

    private val _liveDataUserPhotosList = MutableLiveData<List<UnsplashPhoto>?>()
    val liveDataUserPhotosList: LiveData<List<UnsplashPhoto>?> = _liveDataUserPhotosList

    fun getPhotoDetail(id: String) {
        viewModelScope.launch {
            val response = unsplashRepository.getPhotoDetail(id)
            if (response.isSuccessful) {
                _liveDataList.postValue(response.body())
            }
        }
    }

    fun getUserPhotos(userName: String) {
        viewModelScope.launch {
            try {
                val response = unsplashRepository.getUserPhotos(userName)
                if (!response.isNullOrEmpty()) {
                    _liveDataUserPhotosList.postValue(response)
                }
            } catch (e: Exception) {

            }
        }
    }

}