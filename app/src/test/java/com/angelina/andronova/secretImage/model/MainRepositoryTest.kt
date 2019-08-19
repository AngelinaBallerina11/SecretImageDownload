package com.angelina.andronova.secretImage.model

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class MainRepositoryTest {

    lateinit var repo: MainRepository

    private val imageResponse = ImageResponse("STUB")

    private var service: ImageService = mock()

    @Before
    fun setUp() {
        repo = MainRepository(service)
    }

    @Test
    fun downloadImageSuccess() {
        service.stub {
            on { runBlocking { downloadImageAsync("", "") } } doReturn Response.success(200, imageResponse)
        }
        assertEquals(NetworkCallResult.Success(imageResponse), runBlocking { repo.downloadImage("", "") })
    }

    @Test
    fun downloadImageFailedUnauthorized() {
        service.stub {
            on { runBlocking { downloadImageAsync("", "") } } doReturn Response.error(
                401,
                ResponseBody.create(null, "")
            )
        }
        assertEquals(NetworkCallResult.Failure(HttpErrors.Unauthorized), runBlocking { repo.downloadImage("", "") })
    }

    @Test
    fun downloadImageFailedForbidden() {
        service.stub {
            on { runBlocking { downloadImageAsync("", "") } } doReturn Response.error(
                403,
                ResponseBody.create(null, "")
            )
        }
        assertEquals(NetworkCallResult.Failure(HttpErrors.Forbidden), runBlocking { repo.downloadImage("", "") })
    }

    @Test
    fun downloadImageFailedNotFound() {
        service.stub {
            on { runBlocking { downloadImageAsync("", "") } } doReturn Response.error(
                404,
                ResponseBody.create(null, "")
            )
        }
        assertEquals(NetworkCallResult.Failure(HttpErrors.NotFound), runBlocking { repo.downloadImage("", "") })
    }

    @Test
    fun downloadImageFailedInternalServerError() {
        service.stub {
            on { runBlocking { downloadImageAsync("", "") } } doReturn Response.error(
                500,
                ResponseBody.create(null, "")
            )
        }
        assertEquals(
            NetworkCallResult.Failure(HttpErrors.InternalServerError),
            runBlocking { repo.downloadImage("", "") })
    }

    @Test
    fun downloadImageFailedGeneralError() {
        service.stub {
            on { runBlocking { downloadImageAsync("", "") } } doReturn Response.error(
                415,
                ResponseBody.create(null, "")
            )
        }
        assertEquals(NetworkCallResult.Failure(HttpErrors.General), runBlocking { repo.downloadImage("", "") })
    }
}