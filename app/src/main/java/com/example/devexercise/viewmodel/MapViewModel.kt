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
import com.example.devexercise.viewmodel.impl.MapViewModelImpl
import javax.inject.Inject

class MapViewModel @Inject constructor(application: Application): AndroidViewModel(application), MapViewModelImpl{

    val map = createMap()

    private val deathLayer = ArcgisLayer.deathLayer
    private val casesLayer = ArcgisLayer.casesLayer

    init{
        map.operationalLayers.add(deathLayer)
        map.operationalLayers.add(casesLayer)
        map.loadAsync()
    }

    override fun createMap(): ArcGISMap{
        val baseMap = ArcGISMap(Basemap.createTopographic())
        val initialExtent = Envelope(-99.999999999999929, 40.000000000000057, -99.999999999999929, 40.000000000000057, SpatialReference.create(102100))
        val viewPoint = Viewpoint(initialExtent)
        baseMap.initialViewpoint = viewPoint
        return baseMap
    }

    override fun onCleared() {
        super.onCleared()
        map.operationalLayers.clear()
    }
}