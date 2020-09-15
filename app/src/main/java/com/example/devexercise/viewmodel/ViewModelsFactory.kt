package com.example.devexercise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.network.connection.ConnectionLiveData
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

class HomeViewModelFactory(val repository: CountryRepository, private val connectionLiveData: ConnectionLiveData, private val localPath: String): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, connectionLiveData, localPath) as T
        }
        throw IllegalArgumentException("Unable to construct HomeViewModelFactory")
    }
}
