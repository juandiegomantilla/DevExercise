package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.repository.CountryModel

class CountryMapViewModel(country: CountryModel, application: Application): AndroidViewModel(application){

    private val _mapStatus = MutableLiveData<String>()
    val mapStatus: LiveData<String>
        get() = _mapStatus

    private val _selectedCountry = MutableLiveData<CountryModel>()

    init {
        _selectedCountry.value = country
    }

    val countryMap = createMap(country)

    private val _deathLayer = MutableLiveData<FeatureLayer>()
    private val _casesLayer = MutableLiveData<FeatureLayer>()

    init{
        loadLayers()
        countryMap.operationalLayers.add(_deathLayer.value)
        countryMap.operationalLayers.add(_casesLayer.value )
        countryMap.loadAsync()
    }

    fun createMap(country: CountryModel): ArcGISMap{
        return try{
            val countryMap = ArcGISMap(Basemap.Type.TOPOGRAPHIC, country.Lat!!, country.Long_!!, 16)
            _mapStatus.value = "${country.Country_Region} successfully founded in map"
            countryMap
        }catch (e: Exception){
            val baseMap = ArcGISMap(Basemap.createTopographic())
            val initialExtent = Envelope(-99.999999999999929, 40.000000000000057, -99.999999999999929, 40.000000000000057, SpatialReference.create(102100))
            val viewPoint = Viewpoint(initialExtent)
            baseMap.initialViewpoint = viewPoint

            _mapStatus.value = "Country not founded in map"
            baseMap
        }
    }

    private fun loadLayers(){
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
