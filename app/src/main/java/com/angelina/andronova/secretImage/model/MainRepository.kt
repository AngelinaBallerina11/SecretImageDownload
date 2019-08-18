package com.angelina.andronova.secretImage.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The main interface between the remote API and the ViewModel.
 *
 * @param imageService retrofit service used to fetch an image
 */
class MainRepository(private val imageService: ImageService) {

    /**
     * Method used to connect to Image API and download a secret image based on provided credentials
     *
     * @param username username, usually a surname in plain text
     * @param hashedPassword SHA-1 message digest of the entered password
     *
     * @return NetworkCallResult<ImageResponse> based on response status code
     */
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

/**
 * Generic class representing a network call result
 */
sealed class NetworkCallResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : NetworkCallResult<T>()
    data class Failure(val throwable: Throwable? = null) : NetworkCallResult<Nothing>()
}

/**
 * This calss helps to interpret and categorize HTTP errors received
 */
sealed class HttpErrors : Throwable() {
    object Unauthorized : HttpErrors()
    object Forbidden : HttpErrors()
    object NotFound : HttpErrors()
    object InternalServerError : HttpErrors()
    object General : HttpErrors()
}


