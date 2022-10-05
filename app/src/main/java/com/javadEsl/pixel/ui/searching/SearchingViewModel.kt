package com.javadEsl.pixel.ui.searching

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.api.PixelResponse
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.autocomplete.Suggestion
import com.javadEsl.pixel.data.detail.ModelPhoto
import com.javadEsl.pixel.data.search.PixelPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchingViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper,
    state: SavedStateHandle
) : ViewModel() {

    private val _liveDataRandomPhoto = MutableLiveData<ModelPhoto?>()
    val liveDataRandomPhoto: LiveData<ModelPhoto?> = _liveDataRandomPhoto
    fun getRandomPhoto() {
        viewModelScope.launch {

            if (!networkHelper.hasInternetConnection()) {
                _liveDataRandomPhoto.postValue(null)
                return@launch
            }

            try {
                val response = pixelRepository.getRandomPhoto()
                _liveDataRandomPhoto.postValue(response)
            } catch (e: Exception) {
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


    private val _liveDataSuggestPhoto = MutableLiveData<List<PixelPhoto>>()
    val liveDataSuggestPhoto: LiveData<List<PixelPhoto>> = _liveDataSuggestPhoto
    fun suggestPhotos(suggest: String) {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataSuggestPhoto.postValue(emptyList())
                return@launch
            }

            try {
                val response = pixelRepository.getSuggestPhotos(suggest)
                if (response.results.isNotEmpty()) {
                    _liveDataSuggestPhoto.postValue(response.results)
                }
            } catch (e: Exception) {
                Log.e("TAG", "getAutocomplete: $e")
            }

        }
    }












    private val _liveDataAutocomplete = MutableLiveData<List<Suggestion>?>()
    val liveDataAutocomplete: LiveData<List<Suggestion>?> = _liveDataAutocomplete
    fun getAutocomplete(query: String) {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataAutocomplete.postValue(emptyList())
                return@launch
            }

            try {
                val response = pixelRepository.getAutocomplete(query)
                if (!response.autocomplete.isNullOrEmpty()) {
                    _liveDataAutocomplete.postValue(response.autocomplete)
                }
            } catch (e: Exception) {
                Log.e("TAG", "getAutocomplete: $e")
            }

        }
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val DEFAULT_QUERY = "tehran"
    }
}