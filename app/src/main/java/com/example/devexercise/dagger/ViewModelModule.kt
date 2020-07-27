package com.example.devexercise.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CountryMapViewModel::class)
    abstract fun bindCountryMapViewModel(countryMapViewModel: CountryMapViewModel): ViewModel

    @Binds
    abstract fun bindHomeViewModelFactory(homeViewModelFactory: HomeViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun bindMapViewModelFactory(mapViewModelFactory: MapViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun bindCountryMapViewModelFactory(countryMapViewModelFactory: CountryMapViewModelFactory): ViewModelProvider.Factory
}