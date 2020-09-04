package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.geometry.Geometry
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheParameters
import com.esri.arcgisruntime.tasks.tilecache.ExportTileCacheTask
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asMapRepositoryDomainModel
import com.example.devexercise.network.ArcgisMapService
import com.example.devexercise.network.MapRemoteDataSource
import com.example.devexercise.network.asDatabaseModel
import com.example.devexercise.repository.impl.MapRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MapRepository @Inject constructor(private val database: LocalDatabase, private val service: ArcgisMapService, private val map: MapRemoteDataSource, private val localPath: String): MapRepositoryImpl{

    lateinit var updatedTime: String

    var tileMapToDisplay: ArcGISTiledLayer? = getRemoteMapToLocalMap()
    var offlineMapToDisplay: ArcGISTiledLayer? = null
    private var offlineMapPath = ""
    //private var offlineLayerPath = ""
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int>
        get() = _progress

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

    init {
        if(map.remoteTiledMap != null){
            prepareDownloadPath()
        }
        /*if(map.remoteGeodatabase != null){
            prepareLayerDownloadPath()
            prepareLayersForDownload()
        }*/
    }

    private fun getRemoteMapToLocalMap(): ArcGISTiledLayer? {
        return if (map.remoteTiledMap == null){
            val mapFile = "$localPath/offlineMap/offlineMap.tpk"
            val file = File(mapFile)
            if(file.exists()){
                offlineMapToDisplay = ArcGISTiledLayer(file.absolutePath)
                offlineMapToDisplay
            }else{
                null
            }
        }else{
            map.remoteTiledMap
        }
    }

    private fun prepareDownloadPath(){
        val mapDirectory = "$localPath/offlineMap"
        val mapFile = "offlineMap.tpk"
        val directory = File(mapDirectory)
        if (!directory.exists()){
            directory.mkdir()
        }
        val file = File("$mapDirectory/$mapFile")
        offlineMapPath = file.absolutePath
        println(offlineMapPath)
    }

    fun prepareMapForDownload(downloadArea: Geometry, minScale: Double, maxScale: Double){
        val exportTileCacheTask =  ExportTileCacheTask(map.remoteTiledMap!!.uri)
        exportTileCacheTask.loadAsync()

        exportTileCacheTask.addDoneLoadingListener {
            if(exportTileCacheTask.loadStatus == LoadStatus.LOADED){
                val parametersFuture = exportTileCacheTask.createDefaultExportTileCacheParametersAsync(downloadArea, minScale, maxScale)
                parametersFuture.addDoneListener {
                    try {
                        val parameters= parametersFuture.get()
                        downloadMap(exportTileCacheTask, parameters)
                    } catch (e: Exception){
                        println("Error while preparing for download: $e")
                    }
                }
            }else if(exportTileCacheTask.loadStatus == LoadStatus.FAILED_TO_LOAD){
                println("Load error: ${exportTileCacheTask.loadError}")
            }
        }
    }

    private fun downloadMap(exportTileCacheTask: ExportTileCacheTask, parameters: ExportTileCacheParameters){
        val exportTileCacheJob = exportTileCacheTask.exportTileCacheAsync(parameters, offlineMapPath)
        exportTileCacheJob.addJobChangedListener {
            if(exportTileCacheJob.status == Job.Status.FAILED){
                println("Download error: ${exportTileCacheJob.error}")
            }
        }
        exportTileCacheJob.addProgressChangedListener {
            _progress.value = exportTileCacheJob.progress
        }
        exportTileCacheJob.start()
    }

    /*private fun prepareLayerDownloadPath(){
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

    private fun prepareLayersForDownload(){
        val downloadArea = nyEnvelope
        val geoDatabaseParametersFuture = map.remoteGeodatabase!!.createDefaultGenerateGeodatabaseParametersAsync(downloadArea)
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
            }
        }
    }

    private fun downloadLayers(generateGdbParams: GenerateGeodatabaseParameters){
        val generateGdbJob = map.remoteGeodatabase!!.generateGeodatabase(generateGdbParams, offlineLayerPath)
        generateGdbJob.addJobChangedListener {
            println("Layers download status: " + generateGdbJob.status.name)
            if(generateGdbJob.error != null){
                println("Error downloading layers: " + generateGdbJob.error.message)
            }
            when (generateGdbJob.status) {
                Job.Status.SUCCEEDED -> {
                    println("I DID IT AGAIN!!")
                }
                Job.Status.FAILED -> {
                    println("OH NOT AGAIN!!")
                }
                else -> {
                    println(generateGdbJob.messages[generateGdbJob.messages.size - 1])
                }
            }
        }
        generateGdbJob.start()
        println("Submitted job #" + generateGdbJob.serverJobId + "to create local geodatabase")
    }*/
}
