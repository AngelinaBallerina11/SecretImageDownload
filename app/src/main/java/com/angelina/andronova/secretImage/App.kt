package com.angelina.andronova.secretImage

import android.app.Application
import android.content.Context
import com.angelina.andronova.secretImage.model.PreferenceRepository

class App : Application() {

    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        preferenceRepository = PreferenceRepository(
            getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
        )
    }

    companion object {
        const val DEFAULT_PREFERENCES = "default_preferences"
    }
}