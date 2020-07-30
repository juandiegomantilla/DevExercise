package com.example.devexercise.dagger

import android.app.Application
import com.example.devexercise.repository.CountryModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModelModule {
    @Provides
    @Singleton
    fun provideApplication() = Application()
}