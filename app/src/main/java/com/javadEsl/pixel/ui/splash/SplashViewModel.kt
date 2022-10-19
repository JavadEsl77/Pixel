package com.javadEsl.pixel.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadEsl.pixel.NetworkHelper
import com.javadEsl.pixel.Resource
import com.javadEsl.pixel.data.PixelRepository
import com.javadEsl.pixel.data.update.Update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val pixelRepository: PixelRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {


    private val _connection = MutableLiveData<Boolean?>()
    val connection: LiveData<Boolean?> = _connection

    fun getStartNavigate() {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _connection.postValue(false)
                return@launch
            }

            _connection.postValue(networkHelper.hasInternetConnection())
        }
    }

    private val _checkUpdate = MutableLiveData<Resource<Update>>()
    val checkUpdate: LiveData<Resource<Update>> = _checkUpdate
    fun checkUpdate() {
        viewModelScope.launch {
            if (!networkHelper.hasInternetConnection()) {
                _checkUpdate.postValue(Resource.Failure(message = "check your connection"))
                return@launch
            }
            _checkUpdate.postValue(Resource.Loading)
            try {
                val response = pixelRepository.checkUpdate()
                response.data?.let {
                    _checkUpdate.postValue(Resource.Success(data = it))
                }
            } catch (e: Exception) {
                _checkUpdate.postValue(Resource.Failure(message = e.message.toString()))
            }
        }
    }

}