package com.example.devexercise.dagger

import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.LoginViewModel
import com.example.devexercise.viewmodel.MapViewModel
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideHomeViewModel(dataRepository: CountryRepository): HomeViewModelImpl = HomeViewModel(dataRepository)

    @Provides
    @Singleton
    fun provideMapViewModel(mapRepository: MapRepository): MapViewModel = MapViewModel(mapRepository)

    @Provides
    @Singleton
    fun provideLoginViewModel(loginRepository: LoginRepository): LoginViewModel = LoginViewModel(loginRepository)
}