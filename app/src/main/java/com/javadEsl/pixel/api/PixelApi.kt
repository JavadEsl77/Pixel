package com.javadEsl.pixel.api

import com.javadEsl.pixel.data.allPhotos.AllPhotosItem
import com.javadEsl.pixel.data.autocomplete.AutocompleteModel
import com.javadEsl.pixel.data.detail.ModelPhoto
import com.javadEsl.pixel.data.search.PixelPhoto
import com.javadEsl.pixel.data.topics.TopicsModelItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PixelApi {

    companion object {
        //const val BASE_URL = "https://un.one-developer.ir/napi/"
        //        const val BASE_URL = "https://api.unsplash.com/"

        const val BASE_URL = "https://us.mahdi-saberi.ir/"
        const val CLIENT_ID = "KbhV9-G3KY1l7EtsmS0wI3BgeOVBhjWodZ9Ix9n1Btw"
    }


    @GET("napi/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PixelResponse


    @GET("napi/photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<AllPhotosItem>

    @GET("napi/topics")
    suspend fun getTopics(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<TopicsModelItem>


    @GET("napi/photos/random")
    suspend fun getRandomPhotos(): ModelPhoto


    @GET("napi/photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): Response<ModelPhoto>

    @GET("napi/topics/{id}/photos")
    suspend fun getTopicsPhoto(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<AllPhotosItem>


    @GET("nautocomplete/{query}")
    suspend fun getAutocomplete(
        @Path("query") query: String
    ): AutocompleteModel

    @GET("napi/users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") userName: String
    ): List<PixelPhoto>?
}