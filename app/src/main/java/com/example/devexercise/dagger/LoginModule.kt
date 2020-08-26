package com.example.devexercise.dagger

import android.content.SharedPreferences
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import com.example.devexercise.network.connection.ConnectionLiveData
import dagger.Module
import dagger.Provides

@Module
class LoginModule {
    @Provides
    fun provideLoginLocalDataSource(sharedPreferences: SharedPreferences): LoginLocalDataSource = LoginLocalDataSource.getInstance(sharedPreferences)

    @Provides
    fun provideLoginRemoteDataSource(connectionLiveData: ConnectionLiveData): LoginRemoteDataSource = LoginRemoteDataSource(connectionLiveData)
}