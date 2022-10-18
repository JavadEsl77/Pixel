package com.javadEsl.pixel.ui.searching

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.autocomplete.Suggestion
import com.javadEsl.pixel.data.detail.ModelPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchingViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper,
    private val pref: SharedPreferences,
    state: SavedStateHandle
) : ViewModel() {

    var searchJob: Job? = null
        private set

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


    private val _liveDataSuggestPhoto = MutableLiveData<List<AllPhotosItem>>()
    val liveDataSuggestPhoto: LiveData<List<AllPhotosItem>> = _liveDataSuggestPhoto
    fun suggestPhotos() {
        searchJob = viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _liveDataSuggestPhoto.postValue(emptyList())
                return@launch
            }

            try {

                val response = pixelRepository.getSuggestPhotos()
                if (response.isNotEmpty()) {
                    _liveDataSuggestPhoto.postValue(response)
                } else {
                    _liveDataSuggestPhoto.postValue(emptyList())
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


    fun setSuccessSearchInCash(
        searchValue: String,
        previousSearchArrayList: MutableList<String>
    ) {
        val set: MutableSet<String> = HashSet()
        val editor = pref.edit()
        if (!previousSearchArrayList.contains(searchValue)) {
            previousSearchArrayList.add(searchValue)
            set.addAll(previousSearchArrayList)
            editor?.putStringSet("previousSearch", set)
            editor?.putString("searchValue", searchValue)
            editor?.putString("searchStatus", searchValue)
            editor?.apply()
        } else {
            editor?.putString("searchValue", searchValue)
            editor?.putString("searchStatus", searchValue)
            editor?.apply()
        }
    }

    fun getSearchValueFromCash(): Pair<MutableSet<String>?, String?> {
        val searchValue = pref.getString("searchValue", null)
        val previousSearch = pref.getStringSet("previousSearch", null)
        return Pair(previousSearch, searchValue)
    }

    fun getSearchStatus(): String? {
        return pref.getString("searchStatus", null)
    }

    fun clearSearchStatus() {
        val editor = pref.edit()
        editor.putString("searchStatus", "")
        editor.apply()
    }

    fun clearSearchCash() {
        val editor = pref.edit()
        editor.putString("searchValue", "")
        editor.putStringSet("previousSearch", null)
        editor.putString("searchStatus", "")
        editor.apply()
    }
}