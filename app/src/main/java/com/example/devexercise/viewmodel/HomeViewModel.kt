package com.example.devexercise.viewmodel

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.*
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.getDatabase
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import kotlinx.coroutines.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val dataRepository: CountryRepository): ViewModel(), HomeViewModelImpl {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //private val database = getDatabase(application)

    //private val dataRepository = CountryRepository(database)

    private val _lastUpdate = MutableLiveData<CharSequence>()
    val lastUpdate: LiveData<CharSequence>
        get() = _lastUpdate

    private val _navigateToSelectedCountry = MutableLiveData<CountryModel>()
    val navigateToSelectedCountry: LiveData<CountryModel>
        get() = _navigateToSelectedCountry


    init {
        viewModelScope.launch {
            dataRepository.refreshData()
        }
    }

    override fun getData(){
        viewModelScope.launch {
            dataRepository.refreshData()
        }
        val update = DateUtils.getRelativeTimeSpanString(dataRepository.country.value?.get(1)?.Last_Update!!.toLong())
        _lastUpdate.value = update
    }

    val dataList = dataRepository.country


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun displayCountryOnMap(country: CountryModel) {
        _navigateToSelectedCountry.value = country
    }

    fun displayCountryOnMapComplete() {
        _navigateToSelectedCountry.value = null
    }
}