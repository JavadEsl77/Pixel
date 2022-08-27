package com.javadEsl.pixel.ui.gallery

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.data.ModelPhoto
import com.javadEsl.pixel.data.PixelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper,
    state: SavedStateHandle
) :
    ViewModel() {

    private val _liveDataRandomPhoto = MutableLiveData<ModelPhoto?>()
    val liveDataRandomPhoto: MutableLiveData<ModelPhoto?> = _liveDataRandomPhoto
    fun getRandomPhoto() {
        viewModelScope.launch {
            if (networkHelper.checkConnection()){
                val response = pixelRepository.getRandomPhoto()
                _liveDataRandomPhoto.postValue(response)
            }else{
                _liveDataRandomPhoto.postValue(null)
            }

        }
    }

    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        pixelRepository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "new"
    }

}