package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.example.devexercise.network.ArcgisLayer

class MapViewModel(application: Application): AndroidViewModel(application){
    
    val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, 40.000000000000057, -99.999999999999929, 16)

    private val _deathLayer = MutableLiveData<FeatureLayer>()
    private val _casesLayer = MutableLiveData<FeatureLayer>()

    init{
        loadLayers()
        map.operationalLayers.add(_deathLayer.value)
        map.operationalLayers.add(_casesLayer.value )
        map.loadAsync()
    }

    fun loadLayers(){
        _deathLayer.value = ArcgisLayer.deathLayer
        _casesLayer.value = ArcgisLayer.casesLayer
    }

    override fun onCleared() {
        super.onCleared()
        _deathLayer.value = null
        _casesLayer.value =null
        map.operationalLayers.clear()
    }
}