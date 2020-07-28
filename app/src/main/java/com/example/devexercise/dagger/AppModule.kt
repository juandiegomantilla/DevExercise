package com.example.devexercise.dagger

import android.app.Application
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.getDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, HomeModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideDatabase(application: Application): LocalDatabase{
        return  getDatabase(application)
    }
}