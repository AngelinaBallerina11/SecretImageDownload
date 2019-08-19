package com.angelina.andronova.secretImage.ui.main

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.angelina.andronova.secretImage.R
import com.angelina.andronova.secretImage.model.HttpErrors
import com.angelina.andronova.secretImage.model.MainRepository
import com.angelina.andronova.secretImage.model.NetworkCallResult.Failure
import com.angelina.andronova.secretImage.model.NetworkCallResult.Success
import com.angelina.andronova.secretImage.utils.ConnectionUtils
import com.angelina.andronova.secretImage.utils.HashUtils
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel which ties {@link MainFragment} and {@link MainRepository} together.
 * Provides:
 *      - actions callable by the MainFragment {@link fetchImage}
 *      - active state for the View to bind to {@link screenState}, {@link isButtonEnabled} and {@link imageDownloadResult}
 *
 * {@link isButtonEnabled} is observed via DataBinding
 */
class MainViewModel @Inject constructor(
    private val app: Application,
    private val repo: MainRepository,
    private val hashUtils: HashUtils,
    private val connection: ConnectionUtils
) : AndroidViewModel(app) {

    var screenState = MutableLiveData<ScreenState>()

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

    /**
     * Initiate download of the secret image based on the entered credentials
     * Download will start if:
     *      - the device is online
     *      - the credentials have been entered
     */
    fun fetchImage() {
        if (connection.isOnline()) {
            usernameInput.value?.let { username ->
                passwordInput.value?.let { password ->
                    screenState.value = ScreenState.Loading
                    viewModelScope.launch {
                        downloadImage(username, password)
                    }
                }
            }
        } else {
            screenState.value = ScreenState.Error(app.getString(R.string.you_are_offline))
        }
    }

    suspend fun downloadImage(username: String, password: String) {
        when (val result = repo.downloadImage(username.trim(), hashUtils.toSha1(password))) {
            is Success -> {
                imageDownloadResult.value = hashUtils.decode(result.data.image)
                screenState.value = ScreenState.Idle
            }
            is Failure -> {
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

    /**
     * Sets the global screen state to idle (hides progress bar)
     * Usage: Delete the error state once it has been already displayed to the user
     */
    fun setIdleState() {
        screenState.value = ScreenState.Idle
    }

    /**
     * Validate username and password inputs
     * Validation criteria: both fields should not be empty
     */
    private fun validateInputs() {
        isButtonEnabled.value = !usernameInput.value.isNullOrBlank() && !passwordInput.value.isNullOrBlank()
    }
}

/**
 * Global screen state which controls of the data, error or loading indicator are displayed
 */
sealed class ScreenState {
    object Loading : ScreenState()
    object Idle : ScreenState()
    data class Error(val message: String) : ScreenState()
}
