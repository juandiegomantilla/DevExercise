package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asRepositoryDomainModel
import com.example.devexercise.network.ArcgisApi
import com.example.devexercise.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CountryRepository(private val database: LocalDatabase){
    val country: LiveData<List<CountryModel>> = Transformations.map(database.databaseDao.getCountryFromDatabase()){
        it.asRepositoryDomainModel()
    }

    suspend fun refreshData(){
        withContext(Dispatchers.IO){
            val dataList = ArcgisApi.retrofitService.getArcgisData().await()
            database.databaseDao.insertCountryToDatabase(*dataList.asDatabaseModel())
        }
    }
}