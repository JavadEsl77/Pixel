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

    fun getPhotoDetail(id: String) {
        viewModelScope.launch {
            val response = unsplashRepository.getPhotoDetail(id)
            if (response.isSuccessful) {
                _liveDataList.postValue(response.body())
            }
        }
    }

}