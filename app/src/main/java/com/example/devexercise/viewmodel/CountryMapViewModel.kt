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
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.viewmodel.impl.CountryMapViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class CountryMapViewModel @Inject constructor(private val mapRepository: MapRepository): ViewModel(), CountryMapViewModelImpl{

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private lateinit var controlMap: ArcGISMap

    private val deathLayer = ArcgisLayer.deathLayer
    private val casesLayer = ArcgisLayer.casesLayer

    private val _mapStatus = MutableLiveData<String>()
    val mapStatus: LiveData<String>
        get() = _mapStatus

    private val _lastUpdate = MutableLiveData<CharSequence>()
    val lastUpdate: LiveData<CharSequence>
        get() = _lastUpdate

    init{
        viewModelScope.launch {
            mapRepository.refreshData()
        }
    }

    override fun createMap(country: CountryModel): ArcGISMap{
        return try{
            val countryMap = ArcGISMap(Basemap.Type.TOPOGRAPHIC, country.Lat!!, country.Long_!!, 16)

            addMapLayers(countryMap)

            _mapStatus.value = "${country.Country_Region} successfully founded in map"

            controlMap = countryMap

            countryMap
        }catch (e: Exception){
            val baseMap = ArcGISMap(Basemap.createTopographic())
            val initialExtent = Envelope(-157.498337, -41.4544999999999, 174.886, 64.9631000000001, SpatialReference.create(4326))
            val viewPoint = Viewpoint(initialExtent)
            baseMap.initialViewpoint = viewPoint

            addMapLayers(baseMap)

            _mapStatus.value = "Country not founded in map"

            controlMap = baseMap

            baseMap
        }
    }

    fun refreshMap(){
        addMapLayers(controlMap)
        viewModelScope.launch {
            mapRepository.refreshData()
        }
        _lastUpdate.value = DateUtils.getRelativeTimeSpanString(mapRepository.updatedTime.toLong())
    }

    private fun addMapLayers(map: ArcGISMap){
        if(map.operationalLayers != null){
            map.operationalLayers.clear()
        }
        map.operationalLayers.add(deathLayer.copy())
        map.operationalLayers.add(casesLayer.copy())
        map.loadAsync()
    }

    fun getPointOnMap(envelope: Envelope): ListenableFuture<FeatureQueryResult> {
        val queryParameters = QueryParameters()
        queryParameters.geometry = envelope
        return casesLayer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW)
    }

    fun getMapPointInfo(pointId: Long) = mapRepository.getPointDetails(pointId)

    override fun onCleared() {
        super.onCleared()
        controlMap.operationalLayers.clear()
    }
}
