package com.example.devexercise.dagger

import android.content.SharedPreferences
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoginModule {
    @Provides
    fun provideLoginLocalDataSource(sharedPreferences: SharedPreferences): LoginLocalDataSource = LoginLocalDataSource.getInstance(sharedPreferences)

    @Provides
    fun provideLoginRemoteDataSource(): LoginRemoteDataSource = LoginRemoteDataSource()
}