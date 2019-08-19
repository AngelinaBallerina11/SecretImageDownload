package com.angelina.andronova.secretImage.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject

/**
 * Helper class used to apply SHA-1 hash function (possibly can be extended with other hash functions)
 */
class HashUtils @Inject constructor() {

    fun toSha1(text: String): String {
        val bytes = MessageDigest
            .getInstance(SHA1)
            .digest(text.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach { byte ->
            result.append(HEX_CHARS[byte.toInt() shr 4 and 0x0f])
            result.append(HEX_CHARS[byte.toInt() and 0x0f])
        }
        return result.toString().toLowerCase(Locale.US)
    }

    /**
     * Decode Base64 string to an immutable bitmap
     */
    fun decode(encodedText: String): Bitmap {
        val compressedImageData: ByteArray = android.util.Base64.decode(encodedText, android.util.Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(compressedImageData, 0, compressedImageData.size)
    }

    companion object {
        const val HEX_CHARS = "0123456789ABCDEF"
        const val SHA1 = "SHA-1"
    }
}
