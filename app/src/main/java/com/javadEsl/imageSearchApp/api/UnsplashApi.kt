package com.javadEsl.imageSearchApp.api

import com.javadEsl.imageSearchApp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {

    companion object{
        const val BASE_URL = "https://api.unsplash.com/"
        const val CLIENT_ID = "Fgfj2k6NxRC3kg8DzuOBZrxtxDpjMR9ZxpNBeW4GbYg"
    }

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UnsplashResponse

}