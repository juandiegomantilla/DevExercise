package com.example.devexercise.dagger

import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideHomeViewModel(dataRepository: CountryRepository): HomeViewModelImpl = HomeViewModel(dataRepository)
}