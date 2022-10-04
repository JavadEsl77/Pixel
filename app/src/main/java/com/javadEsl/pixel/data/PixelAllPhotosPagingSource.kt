package com.javadEsl.pixel.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.javadEsl.pixel.api.PixelApi
import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import javax.inject.Inject

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class PixelAllPhotosPagingSource @Inject constructor(
    private val pixelApi: PixelApi
) : PagingSource<Int, AllPhotosItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AllPhotosItem> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
        return try {
            val response = pixelApi.getPhotos(page = position, perPage = params.loadSize)
            val photos = response.toMutableList()
            Log.e("PixelAllPhotosPagingSo", "$response")
            if (photos.isNotEmpty()) {
                photos.add(AllPhotosItem(id = "ad_item", isAdvertisement = true))
            }
            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }


    }

    override fun getRefreshKey(state: PagingState<Int, AllPhotosItem>): Int? {
       return state.anchorPosition
    }
}