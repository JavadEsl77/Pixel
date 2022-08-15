package com.javadEsl.imageSearchApp.ui.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.javadEsl.imageSearchApp.data.UnsplashRepository
import javax.inject.Inject

class GalleryViewModel @Inject constructor(private val unsplashRepository: UnsplashRepository) :
    ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val photos = currentQuery.switchMap { queryString ->
        unsplashRepository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val DEFAULT_QUERY = "cats"
    }

}