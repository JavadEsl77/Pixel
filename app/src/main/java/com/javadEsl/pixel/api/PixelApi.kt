package com.javadEsl.pixel.api

import com.javadEsl.pixel.data.ModelPhoto
import com.javadEsl.pixel.data.UnsplashPhoto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface PixelApi {

    companion object {
        //const val BASE_URL = "https://un.one-developer.ir/napi/"
        //        const val BASE_URL = "https://api.unsplash.com/"

        const val BASE_URL = "https://us.mahdi-saberi.ir/"
        const val CLIENT_ID = "KbhV9-G3KY1l7EtsmS0wI3BgeOVBhjWodZ9Ix9n1Btw"
    }

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("napi/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PixelResponse


    @GET("napi//photos/random")
    suspend fun getRandomPhotos(): ModelPhoto


    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("napi/photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): Response<ModelPhoto>

    @GET("napi/users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") userName: String
    ): List<UnsplashPhoto>?
}