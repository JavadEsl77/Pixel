package com.javadEsl.imageSearchApp.ui.gallery

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.imageSearchApp.NetworkHelper
import com.javadEsl.imageSearchApp.api.UnsplashResponse
import com.javadEsl.imageSearchApp.data.ModelPhoto
import com.javadEsl.imageSearchApp.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val unsplashRepository: UnsplashRepository,
    private val networkHelper: NetworkHelper,
    state: SavedStateHandle
) :
    ViewModel() {

    private val _liveDataRandomPhoto = MutableLiveData<ModelPhoto?>()
    val liveDataRandomPhoto: MutableLiveData<ModelPhoto?> = _liveDataRandomPhoto
    fun getRandomPhoto() {
        viewModelScope.launch {
            if (networkHelper.checkConnection()){
                val response = unsplashRepository.getRandomPhoto()
                _liveDataRandomPhoto.postValue(response)
            }else{
                _liveDataRandomPhoto.postValue(null)
            }

        }
    }

    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        unsplashRepository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "new"
    }

}