package com.angelina.andronova.secretImage.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(private val imageService: ImageService) {

    suspend fun downloadImage(username: String, hashedPassword: String): NetworkCallResult<ImageResponse> {
        val response = withContext(Dispatchers.IO) {
            imageService.downloadImageAsync(username, hashedPassword)
        }
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            NetworkCallResult.Success(data = body)
        } else {
            NetworkCallResult.Failure(
                throwable = when (response.code()) {
                    401 -> HttpErrors.Unauthorized
                    403 -> HttpErrors.Forbidden
                    404 -> HttpErrors.NotFound
                    in 500..599 -> HttpErrors.InternalServerError
                    else -> HttpErrors.General
                }
            )
        }
    }
}

sealed class NetworkCallResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkCallResult<T>()
    data class Failure(val throwable: Throwable? = null) : NetworkCallResult<Nothing>()
}

sealed class HttpErrors : Throwable() {
    object Unauthorized : HttpErrors()
    object Forbidden : HttpErrors()
    object NotFound : HttpErrors()
    object InternalServerError : HttpErrors()
    object General : HttpErrors()
}


