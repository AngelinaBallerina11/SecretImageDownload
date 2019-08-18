package com.angelina.andronova.secretImage.ui.main

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.angelina.andronova.secretImage.R
import com.angelina.andronova.secretImage.model.HashHelper
import com.angelina.andronova.secretImage.model.HttpErrors
import com.angelina.andronova.secretImage.model.MainRepository
import com.angelina.andronova.secretImage.model.NetworkCallResult
import com.angelina.andronova.secretImage.utils.ConnectionUtils
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val app: Application,
    private val repo: MainRepository,
    private val hashHelper: HashHelper,
    private val connection: ConnectionUtils
) : AndroidViewModel(app) {

    val screenState = MutableLiveData<ScreenState>()

    val usernameInput = MutableLiveData<String>()
    val passwordInput = MutableLiveData<String>()
    val isButtonEnabled = MediatorLiveData<Boolean>()
    val imageDownloadResult = MutableLiveData<Bitmap?>()

    init {
        screenState.value = ScreenState.Idle
        with(isButtonEnabled) {
            value = false
            addSource(usernameInput) { validateInputs() }
            addSource(passwordInput) { validateInputs() }
        }
    }

    private fun validateInputs() {
        isButtonEnabled.value = !usernameInput.value.isNullOrBlank() && !passwordInput.value.isNullOrBlank()
    }

    fun fetchImage() {
        if (connection.isOnline()) {
            usernameInput.value?.let { username ->
                passwordInput.value?.let { password ->
                    screenState.value = ScreenState.Loading
                    viewModelScope.launch {
                        when (val result = repo.downloadImage(username.trim(), hashHelper.toSha1(password))) {
                            is NetworkCallResult.Success -> {
                                imageDownloadResult.value = result.data.image.decode()
                                screenState.value = ScreenState.Idle
                            }
                            is NetworkCallResult.Failure -> {
                                screenState.value = ScreenState.Error(
                                    message = app.resources.getString(
                                        when (result.throwable) {
                                            HttpErrors.Unauthorized, HttpErrors.Forbidden -> R.string.you_are_not_authorized
                                            HttpErrors.NotFound -> R.string.file_not_found
                                            HttpErrors.InternalServerError -> R.string.server_error
                                            else -> R.string.generic_error_message
                                        }
                                    )
                                )
                                imageDownloadResult.value = null
                            }
                        }
                    }
                }
            }
        } else {
            screenState.value = ScreenState.Error(app.resources.getString(R.string.you_are_offline))
        }
    }

    private fun String.decode(): Bitmap {
        val decodedString: ByteArray = android.util.Base64.decode(this, android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun setIdleState() {
        screenState.value = ScreenState.Idle
    }
}

sealed class ScreenState {
    object Loading : ScreenState()
    object Idle : ScreenState()
    data class Error(val message: String) : ScreenState()
}
