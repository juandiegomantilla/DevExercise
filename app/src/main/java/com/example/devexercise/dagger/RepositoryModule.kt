package com.example.devexercise.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.network.ArcgisMapService
import com.example.devexercise.network.LoginRemoteDataSource
import com.example.devexercise.network.MapRemoteDataSource
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.util.DateProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    fun provideCountryRepository(database: LocalDatabase, service: ArcgisApiService, sharedPreferences: SharedPreferences, dateProvider: DateProvider): CountryRepository = CountryRepository(database, service, sharedPreferences, dateProvider)

    @Provides
    fun provideMapRepository(database: LocalDatabase, service: ArcgisMapService, map: MapRemoteDataSource, cachePath: String): MapRepository = MapRepository(database, service, map, cachePath)

    @Provides
    fun provideLocalPath(application: Application): String = application.getDir("offlineMode", Context.MODE_PRIVATE).absolutePath

    @Provides
    fun provideLoginRepository(localSource: LoginLocalDataSource, remoteSource: LoginRemoteDataSource): LoginRepository = LoginRepository.getInstance(localSource, remoteSource)
}
