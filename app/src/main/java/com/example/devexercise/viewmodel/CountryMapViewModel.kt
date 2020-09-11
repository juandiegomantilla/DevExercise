package com.example.devexercise.viewmodel

import android.text.format.DateUtils
import androidx.lifecycle.*
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.MapPointModel
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.viewmodel.impl.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class CountryMapViewModel @Inject constructor(private val mapRepository: MapRepository): ViewModel(),
    CreateMapCountry, RefreshMap, AddMapLayers, GetPointOnMap, GetMapPointInfo{

    var isOnline = mapRepository.isOnline

    val downloadStatus = mapRepository.downloadStatus
    val downloadProgress = mapRepository.progress

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private var controlMap: ArcGISMap? = null

    private val deathLayer = ArcgisLayer.deathLayer
    private val casesLayer = ArcgisLayer.casesLayer

    private val _mapStatus = MutableLiveData<String>()
    val mapStatus: LiveData<String>
        get() = _mapStatus

    private val _lastUpdate = MutableLiveData<CharSequence>()
    val lastUpdate: LiveData<CharSequence>
        get() = _lastUpdate

    init{
        if(isOnline.value == true){
            viewModelScope.launch {
                mapRepository.refreshData()
            }
        }
    }

    override fun createMapCountry(country: CountryModel?): ArcGISMap? {

        controlMap = mapRepository.getRemoteMapToLocalMap(country?.Country_Region, country?.Lat, country?.Long_)

        return if(controlMap != null){
            if((country?.Lat != null) && (country.Long_ != null)){
                _mapStatus.value = "${country.Country_Region} successfully found in map"
            }
            addMapLayers(controlMap!!)
            controlMap
        }else{
            null
        }
    }

    fun sendAreaToDownload(country: String?, downloadArea: Geometry, minScale: Double, maxScale: Double){
        var downloadMinScale = minScale
        if(downloadMinScale <= maxScale){
            downloadMinScale = maxScale + 1
        }
        val countryName = country ?: "offlineMap"
        mapRepository.prepareMapForDownload(countryName, downloadArea, downloadMinScale, maxScale)
        //mapRepository.prepareLayersForDownload(downloadArea)
    }

    override fun refreshMap(){
        addMapLayers(controlMap!!)
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

    override fun getPointOnMap(envelope: Envelope): ListenableFuture<FeatureQueryResult> {
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
        viewModelJob.cancel()
    }
}