package com.example.devexercise.viewmodel.impl

import androidx.lifecycle.LiveData
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.example.devexercise.repository.MapPointModel

interface MapViewModelImpl {
    fun createMap(): ArcGISMap
    fun refreshMap()
    fun addMapLayers(map: ArcGISMap)
    fun getPointOnMap(envelope: Envelope): ListenableFuture<FeatureQueryResult>
    fun getMapPointInfo(pointId: Long): LiveData<List<MapPointModel>>
}