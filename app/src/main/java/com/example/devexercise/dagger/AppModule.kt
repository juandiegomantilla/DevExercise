package com.example.devexercise.dagger

import android.app.Application
import android.content.Context
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.getDatabase
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    @Singleton
    fun provideDatabase(): LocalDatabase{ return getDatabase(app) }
}