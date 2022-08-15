package com.javadEsl.imageSearchApp.data

import com.javadEsl.imageSearchApp.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {
}