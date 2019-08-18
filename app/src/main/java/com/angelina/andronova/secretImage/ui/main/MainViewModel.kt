package com.angelina.andronova.secretImage.ui.main

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val usernameInput = MutableLiveData<String>()
    val passwordInput = MutableLiveData<String>()
    val isButtonEnabled = MediatorLiveData<Boolean>()

    init {
        isButtonEnabled.value = false
        isButtonEnabled.addSource(usernameInput) { validateInputs() }
        isButtonEnabled.addSource(passwordInput) { validateInputs() }
    }

    private fun validateInputs() {
        isButtonEnabled.value = !usernameInput.value.isNullOrBlank() && !passwordInput.value.isNullOrBlank()
    }

    fun fetchImage() {
        Log.i("ANGELINA", "click")
    }
}
