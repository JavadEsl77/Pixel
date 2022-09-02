package com.javadEsl.pixel.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadEsl.pixel.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
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

}