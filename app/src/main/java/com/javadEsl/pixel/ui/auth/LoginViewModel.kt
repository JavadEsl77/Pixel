package com.javadEsl.pixel.ui.auth

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javadEsl.pixel.helper.extensions.toTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private var timer: CountDownTimer? = null

    private val _startTime = MutableLiveData<String>()
    val startTime: LiveData<String> = _startTime

    companion object {
        const val FINISHED = "finished"
    }

    fun startTimer() {

        if (timer != null) return

        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _startTime.postValue(millisUntilFinished.toTime())
            }

            override fun onFinish() {
                stopTimer()
                _startTime.postValue(FINISHED)
            }
        }.start()
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
    }
}