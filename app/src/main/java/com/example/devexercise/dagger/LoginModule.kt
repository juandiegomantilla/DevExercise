package com.example.devexercise.dagger

import android.content.SharedPreferences
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import com.example.devexercise.repository.LoginRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoginModule {
    @Provides
    @Singleton
    fun provideLoginLocalDataSource(sharedPreferences: SharedPreferences): LoginLocalDataSource = LoginLocalDataSource.getInstance(sharedPreferences)

    @Provides
    @Singleton
    fun provideLoginRemoteDataSource(): LoginRemoteDataSource = LoginRemoteDataSource()
}