package com.example.devexercise.dagger

import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.repository.CountryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CountryRepositoryModule {
    @Provides
    @Singleton
    fun provideCountryRepository(database: LocalDatabase, service: ArcgisApiService): CountryRepository = CountryRepository(database, service)
}