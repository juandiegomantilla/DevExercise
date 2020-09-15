package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.data.SyncModel
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.tasks.geodatabase.GenerateGeodatabaseParameters
import com.esri.arcgisruntime.tasks.geodatabase.GenerateLayerOption
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheParameters
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheTask
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asMapRepositoryDomainModel
import com.example.devexercise.network.ArcgisMapService
import com.example.devexercise.network.MapRemoteDataSource
import com.example.devexercise.network.asDatabaseModel
import com.example.devexercise.repository.impl.LayerDownloadImpl
import com.example.devexercise.repository.impl.MapRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MapRepository @Inject constructor(private val database: LocalDatabase, private val service: ArcgisMapService, private val map: MapRemoteDataSource, private val localPath: String):
    MapRepositoryImpl, LayerDownloadImpl {

    var isOnline = map.isOnline
    private val worldEnvelope = Envelope(-2.0037507067161843E7, -1.99718688804086E7, 2.0037507067161843E7, 1.9971868880408484E7, SpatialReference.create(3857))

    lateinit var updatedTime: String

    private var offlineMapPath = ""
    private var offlineLayerPath = ""

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int>
        get() = _progress

    private val _downloadStatus = MutableLiveData<String>()
    val downloadStatus: LiveData<String>
        get() = _downloadStatus

    override fun getPointDetails(pointId: Long): LiveData<List<MapPointModel>>{
        return Transformations.map(database.databaseDao.getMapPointFromDatabase(pointId)){
            it.asMapRepositoryDomainModel()
        }
    }

    override suspend fun refreshData(){
        withContext(Dispatchers.IO){
            val dataList = service.getArcgisMapData().await()
            database.databaseDao.insertMapPointToDatabase(*dataList.asDatabaseModel())
            updatedTime = dataList.pointsContainer[1].pointDetails.Last_Update.toString()
        }
    }

    /*init {
        if(map.remoteGeodatabase != null){ prepareLayerDownloadPath() }
    }*/

    override fun getRemoteMapToLocalMap(mapArea: String?, latitude: Double?, longitude: Double?): ArcGISMap? {
        return if (map.remoteTiledMap == null){
            val mapAreaStored: String = mapArea ?: "offlineMap"
            val mapFile = "$localPath/offlineMap/$mapAreaStored.tpk"
            val file = File(mapFile)
            if(file.exists()){
                val offlineMapToDisplay = ArcGISTiledLayer(file.absolutePath)
                ArcGISMap().apply { basemap = Basemap(offlineMapToDisplay) }
            }else{
                null
            }
        }else{
            val mapToDisplay = ArcGISMap().apply { basemap = Basemap(map.remoteTiledMap) }
            if((latitude != null) && (longitude != null)){
                val viewPoint = Viewpoint(latitude, longitude, 6000000.0)
                mapToDisplay.initialViewpoint = viewPoint
            }else{
                mapToDisplay.initialViewpoint = Viewpoint(worldEnvelope)
            }
            return mapToDisplay
        }
    }

    override fun prepareDownloadPath(fileName: String){
        val mapDirectory = "$localPath/offlineMap"
        val mapFile = "$fileName.tpk"
        val directory = File(mapDirectory)
        if (!directory.exists()){
            directory.mkdir()
        }
        val file = File("$mapDirectory/$mapFile")
        offlineMapPath = file.absolutePath
        println(offlineMapPath)
    }

    override fun prepareMapForDownload(countryName: String, downloadArea: Geometry, minScale: Double, maxScale: Double){
        prepareDownloadPath(countryName)
        val exportTileCacheTask =  ExportTileCacheTask(map.remoteTiledMap!!.uri)
        exportTileCacheTask.loadAsync()

        exportTileCacheTask.addDoneLoadingListener {
            if(exportTileCacheTask.loadStatus == LoadStatus.LOADED){
                val parametersFuture = exportTileCacheTask.createDefaultExportTileCacheParametersAsync(downloadArea, minScale, maxScale)
                parametersFuture.addDoneListener {
                    try {
                        val parameters= parametersFuture.get()
                        downloadMap(exportTileCacheTask, parameters)
                        _downloadStatus.value = "PREPARED"
                        _downloadStatus.value = ""
                    } catch (e: Exception){
                        println("Error while preparing for download: $e")
                    }
                }
            }else if(exportTileCacheTask.loadStatus == LoadStatus.FAILED_TO_LOAD){
                println("Load error: ${exportTileCacheTask.loadError}")
            }
        }
    }

    override fun downloadMap(exportTileCacheTask: ExportTileCacheTask, parameters: ExportTileCacheParameters){
        val exportTileCacheJob = exportTileCacheTask.exportTileCacheAsync(parameters, offlineMapPath)
        exportTileCacheJob.addJobChangedListener {
            if(exportTileCacheJob.status == Job.Status.FAILED){
                println("Download error: ${exportTileCacheJob.error}")
                _downloadStatus.value = "FAILED"
                _downloadStatus.value = ""
            }
            if(exportTileCacheJob.status == Job.Status.SUCCEEDED){
                _downloadStatus.value = "SUCCEEDED"
                _downloadStatus.value = ""
            }
        }
        exportTileCacheJob.addProgressChangedListener {
            if(exportTileCacheJob.progress > 3){
                _progress.value = exportTileCacheJob.progress
            }
        }
        exportTileCacheJob.start()
    }

    //Download Layers
    override fun prepareLayerDownloadPath(){
        val layerDirectory = "$localPath/offlineLayers"
        val layerFile = ".geodatabase"
        val directory = File(layerDirectory)
        if (!directory.exists()){
            directory.mkdir()
        }
        val file = File("$layerDirectory/$layerFile")
        offlineLayerPath = file.absolutePath
        println(offlineLayerPath)
    }

    override fun prepareLayersForDownload(downloadArea: Geometry){
        val layerArea = downloadArea
        val geoDatabaseParametersFuture = map.remoteGeodatabase!!.createDefaultGenerateGeodatabaseParametersAsync(layerArea)
        geoDatabaseParametersFuture.addDoneListener {
            try{
                val generateGdbParams = geoDatabaseParametersFuture.get()
                generateGdbParams.syncModel = SyncModel.PER_LAYER
                val deathLayer: Long = 0
                val casesLayer: Long = 1
                generateGdbParams.layerOptions.clear()
                generateGdbParams.layerOptions.add(GenerateLayerOption(deathLayer))
                generateGdbParams.layerOptions.add(GenerateLayerOption(casesLayer))
                generateGdbParams.isReturnAttachments = false
                downloadLayers(generateGdbParams)
            }catch (e: Exception){
                println("Error while preparing for download: $e")
                //com.esri.arcgisruntime.ArcGISRuntimeException: Invalid response: The feature service does not support geodatabase sync.
            }
        }
    }

    override fun downloadLayers(generateGdbParams: GenerateGeodatabaseParameters){
        val generateGdbJob = map.remoteGeodatabase!!.generateGeodatabase(generateGdbParams, offlineLayerPath)
        generateGdbJob.addJobChangedListener {
            println("Layers download status: " + generateGdbJob.status.name)
            if(generateGdbJob.error != null){ println("Error downloading layers: " + generateGdbJob.error.message) }
            when (generateGdbJob.status) {
                Job.Status.SUCCEEDED -> println("Download success")
                Job.Status.FAILED -> println("Download failed")
                else -> println(generateGdbJob.messages[generateGdbJob.messages.size - 1])
            }
        }
        generateGdbJob.start()
    }
}