package com.example.devexercise.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.getDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, ArcgisApiServiceModule::class, LoginModule::class, RepositoryModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): LocalDatabase = getDatabase(application)

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences = application.getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
}