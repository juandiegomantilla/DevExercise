package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.repository.CountryModel

class CountryMapViewModel(country: CountryModel, application: Application): AndroidViewModel(application){

    private val _selectedCountry = MutableLiveData<CountryModel>()

    init {
        _selectedCountry.value = country
    }

    val countryMap = ArcGISMap(Basemap.Type.TOPOGRAPHIC, country.Lat!!, country.Long_!!, 16)

    private val _deathLayer = MutableLiveData<FeatureLayer>()
    private val _casesLayer = MutableLiveData<FeatureLayer>()

    init{
        loadLayers()
        countryMap.operationalLayers.add(_deathLayer.value)
        countryMap.operationalLayers.add(_casesLayer.value )
        countryMap.loadAsync()
    }

    fun loadLayers(){
        _deathLayer.value = ArcgisLayer.deathLayer
        _casesLayer.value = ArcgisLayer.casesLayer
    }

    override fun onCleared() {
        super.onCleared()
        _deathLayer.value = null
        _casesLayer.value =null
        countryMap.operationalLayers.clear()
    }
}
