package com.example.devexercise.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asRepositoryDomainModel
import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.network.asDatabaseModel
import com.example.devexercise.repository.impl.CountryRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryRepository @Inject constructor(private val database: LocalDatabase, private val service: ArcgisApiService): CountryRepositoryImpl{
    val country: LiveData<List<CountryModel>> = Transformations.map(database.databaseDao.getCountryFromDatabase()){
        it.asRepositoryDomainModel()
    }

    override suspend fun refreshData(){
        withContext(Dispatchers.IO){
            val dataList = service.getArcgisData().await()
            database.databaseDao.insertCountryToDatabase(*dataList.asDatabaseModel())
        }
    }
}