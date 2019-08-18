package com.angelina.andronova.secretImage.di.modules

import android.app.Application
import android.content.Context
import com.angelina.andronova.secretImage.model.ImageService
import com.angelina.andronova.secretImage.model.MainRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    internal fun provideApplication(): Application = application

    @Provides
    internal fun provideContext(): Context = application.applicationContext

    @Provides
    fun provideMainRepository(imageService: ImageService): MainRepository = MainRepository(imageService)
}