package com.example.devexercise.dagger

import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.network.ArcgisMapService
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.repository.MapRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideCountryRepository(database: LocalDatabase, service: ArcgisApiService): CountryRepository = CountryRepository(database, service)

    @Provides
    @Singleton
    fun provideMapRepository(database: LocalDatabase, service: ArcgisMapService): MapRepository = MapRepository(database, service)
}