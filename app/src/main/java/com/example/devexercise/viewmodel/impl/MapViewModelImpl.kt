package com.example.devexercise.viewmodel.impl

import androidx.lifecycle.LiveData
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.MapPointModel

interface CreateMapCountry {
    fun createMap(country: CountryModel): ArcGISMap
}

interface CreateWorldMap {
    fun createMap(): ArcGISMap
}

interface RefreshMap {
    fun refreshMap()
}

interface AddMapLayers {
    fun addMapLayers(map: ArcGISMap)
}

interface GetPointOnMap {
    fun getPointOnMap(envelope: Envelope): ListenableFuture<FeatureQueryResult>
}

interface GetMapPointInfo {
    fun getMapPointInfo(pointSelectedOnMap: FeatureQueryResult): LiveData<List<MapPointModel>>
}