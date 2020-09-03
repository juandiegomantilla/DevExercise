package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.geometry.SpatialReference
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
    private val nyEnvelope = Envelope(-8259221.806896, 4727458.643225, -7957943.689966, 5230770.320920, SpatialReference.create(3857))
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
            prepareMapForDownload()
        }
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

    private fun prepareMapForDownload(){
        val downloadArea = nyEnvelope
        val exportTileCacheTask =  ExportTileCacheTask(map.remoteTiledMap!!.uri)
        exportTileCacheTask.loadAsync()

        exportTileCacheTask.addDoneLoadingListener {
            if(exportTileCacheTask.loadStatus == LoadStatus.LOADED){
                val parametersFuture = exportTileCacheTask.createDefaultExportTileCacheParametersAsync(downloadArea, 2000000.0, 1000000.0)
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
}
