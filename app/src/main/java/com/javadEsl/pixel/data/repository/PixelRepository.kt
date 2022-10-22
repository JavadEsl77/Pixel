package com.javadEsl.pixel.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.javadEsl.pixel.api.PixelApi
import com.javadEsl.pixel.data.PixelAllPhotosPagingSource
import com.javadEsl.pixel.data.UnsplashPagingSource
import com.javadEsl.pixel.ui.gallery.PixelTopicsPhotoPagingSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixelRepository @Inject constructor(
    private val pixelApi: PixelApi
) {

    fun getAllPhotos() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PixelAllPhotosPagingSource(pixelApi)
        }
    ).liveData

    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UnsplashPagingSource(pixelApi, query)
            }
        ).liveData

    suspend fun getRandomPhoto() = pixelApi.getRandomPhotos()

    suspend fun getPhotoDetail(id: String) = pixelApi.getPhoto(id = id)

    suspend fun getUserPhotos(userName: String) = pixelApi.getUserPhotos(userName = userName)
        ?.filter {
            it.premium != true
        }

    suspend fun getAutocomplete(query: String) = pixelApi.getAutocomplete(query = query)

    suspend fun getSuggestPhotos() =
        pixelApi.getPhotos(page = 2, perPage = 7, order_by = "popular").filter {
            it.premium != true
        }

    suspend fun getTopics() = pixelApi.getTopics(page = 1, perPage = 20)

    fun getTopicsPhotos(topicId: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PixelTopicsPhotoPagingSource(pixelApi, topicId)
        }
    ).liveData

    suspend fun checkUpdate() = pixelApi.checkUpdate()

}