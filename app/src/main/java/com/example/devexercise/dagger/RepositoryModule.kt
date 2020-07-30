package com.example.devexercise.dagger

import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.repository.CountryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(database: LocalDatabase): CountryRepository = CountryRepository(database)
}