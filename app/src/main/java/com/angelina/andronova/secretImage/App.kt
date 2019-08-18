package com.angelina.andronova.secretImage

import android.app.Application
import android.content.Context
import com.angelina.andronova.secretImage.di.AppComponent
import com.angelina.andronova.secretImage.di.DaggerAppComponent
import com.angelina.andronova.secretImage.di.modules.AppModule
import com.angelina.andronova.secretImage.model.PreferenceRepository
import com.angelina.andronova.secretImage.di.modules.NetworkModule

class App : Application() {

    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        preferenceRepository = PreferenceRepository(
            getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
        )
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .networkModule(NetworkModule(BASE_URL))
            .build()
    }

    companion object {
        const val DEFAULT_PREFERENCES = "default_preferences"
        private const val BASE_URL: String = "https://mobility.cleverlance.com"
        lateinit var component: AppComponent
    }
}