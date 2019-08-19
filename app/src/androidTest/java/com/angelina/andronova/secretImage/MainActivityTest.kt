package com.angelina.andronova.secretImage

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelina.andronova.secretImage.EspressoTestMatchers.withDrawable
import com.angelina.andronova.secretImage.ui.main.MainActivity
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private var idlingResource: IdlingResource? = null

    @Before
    fun setUp() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            idlingResource = activity.getIdlingResource()
            // To prove that the test fails, omit this call:
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @After
    fun unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }

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