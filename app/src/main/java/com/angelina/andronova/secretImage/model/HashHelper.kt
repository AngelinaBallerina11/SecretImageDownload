package com.angelina.andronova.secretImage.model

import java.security.MessageDigest
import javax.inject.Inject

class HashHelper @Inject constructor() {

    fun toSha1(text: String): String {
        val bytes = MessageDigest
            .getInstance(SHA1)
            .digest(text.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach { byte ->
            result.append(HEX_CHARS[byte.toInt() shr 4 and 0x0f])
            result.append(HEX_CHARS[byte.toInt() and 0x0f])
        }
        return result.toString()
    }

    companion object {
        const val HEX_CHARS = "0123456789ABCDEF"
        const val SHA1 = "SHA-1"
    }
}
