package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asMapRepositoryDomainModel
import com.example.devexercise.network.ArcgisMapService
import com.example.devexercise.network.asDatabaseModel
import com.example.devexercise.repository.impl.MapRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MapRepository @Inject constructor(private val database: LocalDatabase, private val service: ArcgisMapService): MapRepositoryImpl{

    lateinit var updatedTime: String

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
}