package com.angelina.andronova.secretImage

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.angelina.andronova.secretImage.EspressoTestMatchers.withDrawable
import com.angelina.andronova.secretImage.ui.main.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun whenPasswordIsNotEnteredButtonIsNotEnabled() {
        onView(withId(R.id.etUsername)).perform(typeText("hello"))
        onView(withId(R.id.btLogin)).check(matches(not(isEnabled())))
    }

    @Test
    fun whenUsernameIsNotEnteredButtonIsNotEnabled() {
        onView(withId(R.id.etPassword)).perform(typeText("hello"))
        onView(withId(R.id.btLogin)).check(matches(not(isEnabled())))
    }


    @Test
    fun whenBothUsernameAndPasswordAreEnteredButtonIsEnabled() {
        onView(withId(R.id.etUsername)).perform(typeText("hello"))
        onView(withId(R.id.etPassword)).perform(typeText("hello"))
        onView(withId(R.id.btLogin)).check(matches(isEnabled()))
    }

    @Test
    fun initiallyThereIsPlaceholderImage() {
        onView(withId(R.id.ivPlaceholder)).check(matches(withDrawable(R.drawable.ic_image_placeholder)))
    }

    @Test
    fun placeholderDisappearsOnLoad() {
        onView(withId(R.id.etUsername)).perform(typeText("andronova "))
        onView(withId(R.id.etPassword)).perform(typeText("angelina"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.btLogin)).perform(click())
        onView(withId(R.id.ivPlaceholder)).check(matches(not(withDrawable(R.drawable.ic_image_placeholder))))
    }
}