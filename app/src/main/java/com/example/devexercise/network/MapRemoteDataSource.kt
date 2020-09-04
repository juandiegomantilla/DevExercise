package com.example.devexercise.network

import androidx.lifecycle.Observer
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.tasks.geodatabase.GeodatabaseSyncTask
import com.example.devexercise.network.connection.ConnectionLiveData
import javax.inject.Inject

class MapRemoteDataSource @Inject constructor(private val connectionLiveData: ConnectionLiveData) {
    private val tiledMap = ArcGISTiledLayer("https://tiledbasemaps.arcgis.com/arcgis/rest/services/World_Street_Map/MapServer")
    //private val geodatabaseSyncTask = GeodatabaseSyncTask("https://services1.arcgis.com/0MSEUqKaxRlEPj5g/arcgis/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer")

    var remoteTiledMap: ArcGISTiledLayer? = null
    //var remoteGeodatabase: GeodatabaseSyncTask? = null

    init {
        checkConnection()
    }

    private fun checkConnection() {
        val connectionObserver = Observer<Boolean> {
            remoteTiledMap = if(it){ tiledMap }else{ null }
            //remoteGeodatabase = if(it){ geodatabaseSyncTask }else{ null }
        }
        connectionLiveData.observeForever(connectionObserver)
    }
}
