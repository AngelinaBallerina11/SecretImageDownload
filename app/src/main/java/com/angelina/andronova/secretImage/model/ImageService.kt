package com.angelina.andronova.secretImage.model

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit service to fetch the secret image
 */
interface ImageService {

    companion object {
        const val usernameField = "username"
        const val authorizationHeader = "Authorization"
        const val imageDownloadEndpoint = "download/bootcamp/image.php"
    }

    /**
     * Method used to connect to Image API and download a secret image based on provided credentials
     *
     * @param username username, usually a surname in plain text
     * @param hashedPassword SHA-1 message digest of the entered password
     *
     * @return Response<ImageResponse>
     */
    @FormUrlEncoded
    @POST(imageDownloadEndpoint)
    suspend fun downloadImageAsync(
        @Field(usernameField) username: String,
        @Header(authorizationHeader) hashedPassword: String
    ): Response<ImageResponse>
}

/**
 * Data class used to hold the successful HTTP response of the {@link #imageDownloadEndpoint}
 */
data class ImageResponse(val image: String)