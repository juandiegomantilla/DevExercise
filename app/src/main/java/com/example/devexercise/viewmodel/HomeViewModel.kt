package com.example.devexercise.viewmodel

import android.text.format.DateUtils
import androidx.lifecycle.*
import com.example.devexercise.network.connection.ConnectionLiveData
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import kotlinx.coroutines.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val dataRepository: CountryRepository, private val connectionLiveData: ConnectionLiveData): ViewModel(), HomeViewModelImpl {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _lastUpdate = MutableLiveData<CharSequence>()
    val lastUpdate: LiveData<CharSequence>
        get() = _lastUpdate

    private val _navigateToSelectedCountry = MutableLiveData<CountryModel>()
    val navigateToSelectedCountry: LiveData<CountryModel>
        get() = _navigateToSelectedCountry

    private val _isOnline = MutableLiveData<Boolean>()
    val isOnline: LiveData<Boolean>
        get() = _isOnline


    init {
        checkConnection()
        if(_isOnline.value == true){
            viewModelScope.launch {
                dataRepository.refreshData()
            }
        }
    }

    override fun getData(){
        if(_isOnline.value == true){
            viewModelScope.launch {
                dataRepository.refreshData()
            }
            val update = DateUtils.getRelativeTimeSpanString(dataRepository.country.value?.get(1)?.Last_Update!!.toLong())
            _lastUpdate.value = "Last server update: $update"
        }else{
            val update = "You are offline now."
            _lastUpdate.value = update
        }
    }

    val dataList = dataRepository.country

    override fun displayCountryOnMap(country: CountryModel) {
        _navigateToSelectedCountry.value = country
    }

    override fun displayCountryOnMapComplete() {
        _navigateToSelectedCountry.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    override fun checkConnection() {
        val connectionObserver = Observer<Boolean> {
            _isOnline.value = it
        }
        connectionLiveData.observeForever(connectionObserver)
    }
}