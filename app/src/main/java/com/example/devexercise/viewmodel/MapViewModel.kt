package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.example.devexercise.database.getDatabase
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.repository.MapPointModel
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.viewmodel.impl.MapViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel(application: Application): AndroidViewModel(application), MapViewModelImpl{

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val database = getDatabase(application)
    private val mapRepository = MapRepository(database)

    val map = createMap()

    private val deathLayer = ArcgisLayer.deathLayer
    private val casesLayer = ArcgisLayer.casesLayer

    init{
        map.operationalLayers.add(deathLayer)
        map.operationalLayers.add(casesLayer)
        map.loadAsync()
        viewModelScope.launch {
            mapRepository.refreshData()
        }
    }

    fun mapDataList(pointId: Long): LiveData<List<MapPointModel>>{
        return mapRepository.getPointDetails(pointId)
    }

    fun getFeatureQueryResult(envelope: Envelope): ListenableFuture<FeatureQueryResult>{
        val queryParameters = QueryParameters()
        queryParameters.geometry = envelope
        return casesLayer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW)
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
        viewModelJob.cancel()
    }
}