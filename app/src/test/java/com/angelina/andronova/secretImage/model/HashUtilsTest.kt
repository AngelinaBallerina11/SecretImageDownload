package com.angelina.andronova.secretImage.model

import com.angelina.andronova.secretImage.utils.HashUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HashUtilsTest {

    lateinit var utils: HashUtils


    @Before
    fun setUp() {
        utils = HashUtils()
    }

    @Test
    fun testSha1() {
        val text = "ABC"
        val hashedText = "3c01bdbb26f358bab27f267924aa2c9a03fcfdb8"
        assertEquals(utils.toSha1(text), hashedText)
    }
}