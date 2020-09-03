package com.example.devexercise.network

import androidx.lifecycle.Observer
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.example.devexercise.network.connection.ConnectionLiveData
import javax.inject.Inject

class MapRemoteDataSource @Inject constructor(private val connectionLiveData: ConnectionLiveData) {
    private val tiledMap = ArcGISTiledLayer("https://tiledbasemaps.arcgis.com/arcgis/rest/services/World_Street_Map/MapServer")

    var remoteTiledMap: ArcGISTiledLayer? = null

    init {
        checkConnection()
    }

    private fun checkConnection() {
        val connectionObserver = Observer<Boolean> {
            remoteTiledMap = if(it){ tiledMap }else{ null }
        }
        connectionLiveData.observeForever(connectionObserver)
    }
}
