package com.angelina.andronova.secretImage.ui.main

import android.app.Application
import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.angelina.andronova.secretImage.R
import com.angelina.andronova.secretImage.model.ImageResponse
import com.angelina.andronova.secretImage.model.MainRepository
import com.angelina.andronova.secretImage.model.NetworkCallResult
import com.angelina.andronova.secretImage.utils.ConnectionUtils
import com.angelina.andronova.secretImage.utils.HashUtils
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    lateinit var viewModel: MainViewModel

    private var repo = mock<MainRepository>()
    private var hashUtils = mock<HashUtils>()
    private var connection = mock<ConnectionUtils>()
    private var app = mock<Application>()
    private var mockBitmap = mock<Bitmap>()

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MainViewModel(app, repo, hashUtils, connection)
        viewModel.screenState = MutableLiveData()
    }

    @Test
    fun fetchImageOffline() {
        val errorMessage = "You are not connected to the internet. Please, check your connection and try again later."
        connection.stub {
            on { isOnline() } doReturn false
        }
        app.stub {
            on { getString(R.string.you_are_offline) } doReturn errorMessage
        }
        viewModel.fetchImage()
        Assert.assertEquals(
            ScreenState.Error(errorMessage),
            viewModel.screenState.value
        )
    }

    @Test
    fun fetchImageOnlineStartsLoading() {
        connection.stub {
            on { isOnline() } doReturn true
        }
        viewModel.usernameInput.value = "123"
        viewModel.passwordInput.value = "123"
        viewModel.fetchImage()
        Assert.assertEquals(ScreenState.Loading, viewModel.screenState.value)
    }

    @Test
    fun downloadImage() {
        val encodedImageStub = "STUB"
        repo.stub {
            on { runBlocking { downloadImage("", "") } } doReturn NetworkCallResult.Success(
                ImageResponse(
                    encodedImageStub
                )
            )
        }
        doReturn("").`when`(hashUtils).toSha1("")
        doReturn(mockBitmap).`when`(hashUtils).decode(encodedImageStub)
        runBlocking { viewModel.downloadImage("", "") }
        Assert.assertEquals(ScreenState.Idle, viewModel.screenState.value)
        Assert.assertNotNull(viewModel.imageDownloadResult.value)
    }

    @Test
    fun isButtonDisabledWhenCredentialsAreNotEntered() {
        viewModel.isButtonEnabled.observeForever {  }
        viewModel.usernameInput.value = ""
        viewModel.passwordInput.value = ""
        Assert.assertEquals(false, viewModel.isButtonEnabled.value)
    }

    @Test
    fun isButtonEnabledWhenCredentialsAreEntered() {
        viewModel.isButtonEnabled.observeForever {  }
        viewModel.usernameInput.value = "username"
        viewModel.passwordInput.value = "password"
        Assert.assertEquals(true, viewModel.isButtonEnabled.value)
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }
}