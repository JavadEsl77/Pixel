package com.javadEsl.pixel.di

import android.content.Context
import android.content.SharedPreferences
import com.javadEsl.pixel.BuildConfig
import com.javadEsl.pixel.R
import com.javadEsl.pixel.api.PixelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(PixelApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): PixelApi =
        retrofit.create(PixelApi::class.java)

    @Provides
    @Singleton
    fun provideFolder(@ApplicationContext context: Context): String =
        context.getString(R.string.app_name)

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.applicationContext.getSharedPreferences(
        BuildConfig.APPLICATION_ID,
        Context.MODE_PRIVATE
    )

}