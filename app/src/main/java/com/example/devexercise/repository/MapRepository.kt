package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asMapRepositoryDomainModel
import com.example.devexercise.network.ArcgisMapPoints
import com.example.devexercise.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapRepository(private val database: LocalDatabase){

    fun getPointDetails(pointId: Long): LiveData<List<MapPointModel>>{
        return Transformations.map(database.databaseDao.getMapPointFromDatabase(pointId)){
            it.asMapRepositoryDomainModel()
        }
    }

    suspend fun refreshData(){
        withContext(Dispatchers.IO){
            val dataList = ArcgisMapPoints.retrofitMapService.getArcgisMapData().await()
            database.databaseDao.insertMapPointToDatabase(*dataList.asDatabaseModel())
        }
    }
}