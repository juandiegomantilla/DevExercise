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

class MapViewModel(application: Application): AndroidViewModel(application){

    private val _map = MutableLiveData<ArcGISMap>()
    val map: LiveData<ArcGISMap>
        get() = _map

    private val _deathLayer = MutableLiveData<FeatureLayer>()
    private val _casesLayer = MutableLiveData<FeatureLayer>()

    init{
        loadMap()
    }

    fun loadMap(){
        _map.value = ArcGISMap(Basemap.createTopographic())
        val initialExtent = Envelope(-99.999999999999929, 40.000000000000057, -99.999999999999929, 40.000000000000057, SpatialReference.create(102100))
        val viewPoint = Viewpoint(initialExtent)

        _map.value?.initialViewpoint = viewPoint

        _deathLayer.value = ArcgisLayer.deathLayer
        _casesLayer.value = ArcgisLayer.casesLayer

        _map.value?.operationalLayers?.add(_deathLayer.value)
        _map.value?.operationalLayers?.add(_casesLayer.value)

        _map.value?.loadAsync()
    }

    override fun onCleared() {
        super.onCleared()
        _deathLayer.value = null
        _casesLayer.value = null
        _map.value?.operationalLayers?.clear()
    }
}