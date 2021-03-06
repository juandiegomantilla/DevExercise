package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.repository.MapRepository

class CountryMapViewModelFactory(private val repository: MapRepository) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountryMapViewModel::class.java)) {
            return CountryMapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct CountryMapViewModel")
    }
}

class HomeViewModelFactory(val repository: CountryRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct HomeViewModelFactory")
    }
}

class MapViewModelFactory(val repository: MapRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unable to construct MapViewModelFactory")
    }
}