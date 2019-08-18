package com.angelina.andronova.secretImage.di

import com.angelina.andronova.secretImage.ui.main.MainFragment
import com.angelina.andronova.secretImage.di.modules.AppModule
import com.angelina.andronova.secretImage.di.modules.NetworkModule
import com.angelina.andronova.secretImage.di.modules.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(mainFragment: MainFragment)
}