package com.example.devexercise.repository.impl

import androidx.lifecycle.LiveData
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheParameters
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheTask
import com.example.devexercise.repository.MapPointModel

interface MapRepositoryImpl {
    suspend fun refreshData()
    fun getPointDetails(pointId: Long): LiveData<List<MapPointModel>>
    fun getRemoteMapToLocalMap(mapArea: String?, latitude: Double?, longitude: Double?): ArcGISMap?
    fun prepareDownloadPath(fileName: String)
    fun prepareMapForDownload(countryName: String, downloadArea: Geometry, minScale: Double, maxScale: Double)
    fun downloadMap(exportTileCacheTask: ExportTileCacheTask, parameters: ExportTileCacheParameters)
}

interface LayerDownloadImpl {
    fun prepareLayerDownloadPath()
    fun prepareLayersForDownload(downloadArea: Geometry)
    fun downloadLayers(generateGdbParams: GenerateGeodatabaseParameters)
}