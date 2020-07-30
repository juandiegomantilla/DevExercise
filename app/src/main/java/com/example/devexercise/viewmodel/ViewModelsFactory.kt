package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.repository.CountryModel

class CountryMapViewModelFactory(private val country: CountryModel, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountryMapViewModel::class.java)) {
            return CountryMapViewModel(country, application) as T
        }
        throw IllegalArgumentException("Unable to construct CountryMapViewModel")
    }
}

class HomeViewModelFactory(val database: LocalDatabase): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(database) as T
        }
        throw IllegalArgumentException("Unable to construct HomeViewModelFactory")
    }
}

class MapViewModelFactory(val app: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct MapViewModelFactory")
    }
}