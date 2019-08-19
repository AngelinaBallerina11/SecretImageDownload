package com.angelina.andronova.secretImage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


object EspressoTestMatchers {

    /**
     * Used to check if specific drawable is set in the ImageView
     *
     * @param expectedId resource id of the expected drawable
     */
    fun withDrawable(expectedId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {}

            override fun matchesSafely(item: View?): Boolean {
                if (item !is AppCompatImageView) {
                    return false
                }
                val imageView = item as AppCompatImageView
                if (expectedId < 0) {
                    return imageView.drawable == null
                }
                val expectedDrawable = ContextCompat.getDrawable(item.context, expectedId) ?: return false
                val bitmap = getBitmap(item.drawable)
                val otherBitmap = getBitmap(expectedDrawable)
                return bitmap.sameAs(otherBitmap)
            }

        }
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}