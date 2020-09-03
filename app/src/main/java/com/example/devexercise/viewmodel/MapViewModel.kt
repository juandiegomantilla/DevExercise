package com.example.devexercise.viewmodel

import android.text.format.DateUtils
import androidx.lifecycle.*
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.network.connection.ConnectionLiveData
import com.example.devexercise.repository.MapPointModel
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.viewmodel.impl.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel @Inject constructor(private val mapRepository: MapRepository, private val connectionLiveData: ConnectionLiveData): ViewModel(),
    CreateWorldMap, RefreshMap, AddMapLayers, GetPointOnMap, GetMapPointInfo{

    val tiledMap = mapRepository.tileMapToDisplay
    //private val worldEnvelope = Envelope(-2.0037507067161843E7, -1.99718688804086E7, 2.0037507067161843E7, 1.9971868880408484E7, SpatialReference.create(3857))
    private val nyEnvelope = Envelope(-8259221.806896, 4727458.643225, -7957943.689966, 5230770.320920, SpatialReference.create(3857))
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val map = createMap()

    private val deathLayer = ArcgisLayer.deathLayer
    private val casesLayer = ArcgisLayer.casesLayer

    private val _lastUpdate = MutableLiveData<CharSequence>()
    val lastUpdate: LiveData<CharSequence>
        get() = _lastUpdate

    private val _isOnline = MutableLiveData<Boolean>()
    val isOnline: LiveData<Boolean>
        get() = _isOnline

    init{
        checkConnection()
        if(_isOnline.value == true){
            addMapLayers(map)
            viewModelScope.launch {
                mapRepository.refreshData()
            }
        }
    }

    override fun createMap(): ArcGISMap{
        //val baseMap = ArcGISMap(Basemap.createTopographic())
        val baseMap = ArcGISMap().apply { basemap = Basemap(tiledMap) }
        val initialExtent = nyEnvelope
        val viewPoint = Viewpoint(initialExtent)
        baseMap.initialViewpoint = viewPoint
        return baseMap
    }

    override fun refreshMap(){
        addMapLayers(map)
        viewModelScope.launch {
            mapRepository.refreshData()
        }
        _lastUpdate.value = DateUtils.getRelativeTimeSpanString(mapRepository.updatedTime.toLong())
    }

    override fun addMapLayers(map: ArcGISMap){
        if(map.operationalLayers != null){
            map.operationalLayers.clear()
        }
        map.operationalLayers.add(deathLayer.copy())
        map.operationalLayers.add(casesLayer.copy())
        map.loadAsync()
    }

    override fun getPointOnMap(envelope: Envelope): ListenableFuture<FeatureQueryResult>{
        val queryParameters = QueryParameters()
        queryParameters.geometry = envelope
        return casesLayer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW)
    }

     override fun getMapPointInfo(pointSelectedOnMap: FeatureQueryResult): LiveData<List<MapPointModel>> {
        val iterator = pointSelectedOnMap.iterator()
        val feature = iterator.next()
        val attr = feature.attributes
        val pointId = attr["OBJECTID"] as Long
        return mapRepository.getPointDetails(pointId)
    }

    override fun onCleared() {
        super.onCleared()
        if(tiledMap != null){
            map.operationalLayers.clear()
            viewModelJob.cancel()
        }
    }

    private fun checkConnection() {
        val connectionObserver = Observer<Boolean> {
            _isOnline.value = it
        }
        connectionLiveData.observeForever(connectionObserver)
    }
}
