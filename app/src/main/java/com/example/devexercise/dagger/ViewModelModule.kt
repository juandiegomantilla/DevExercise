package com.example.devexercise.dagger

import android.app.Application
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.viewmodel.CountryMapViewModel
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.MapViewModel
import com.example.devexercise.viewmodel.impl.CountryMapViewModelImpl
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import com.example.devexercise.viewmodel.impl.MapViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Provides
    @Singleton
    fun provideMapViewModel(app: Application): MapViewModelImpl = MapViewModel(app)
}