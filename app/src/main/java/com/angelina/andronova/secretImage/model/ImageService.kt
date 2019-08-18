package com.angelina.andronova.secretImage.model

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface ImageService {

    companion object {
        const val usernameField = "username"
        const val authorizationHeader = "Authorization"
        const val imageDownloadPath = "download/bootcamp/image.php"
    }

    @FormUrlEncoded
    @POST(imageDownloadPath)
    suspend fun downloadImageAsync(
        @Field(usernameField) username: String,
        @Header(authorizationHeader) hashedPassword: String
    ): Response<ImageResponse>
}

data class ImageResponse(val image: String)