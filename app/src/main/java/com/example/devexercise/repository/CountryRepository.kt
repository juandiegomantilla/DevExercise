package com.example.devexercise.repository

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.devexercise.database.LocalDatabase
import com.example.devexercise.database.asRepositoryDomainModel
import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.network.asDatabaseModel
import com.example.devexercise.repository.impl.CountryRepositoryImpl
import com.example.devexercise.util.DateProvider
import com.example.devexercise.util.EXPIRY_TIME_DATA
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryRepository @Inject constructor(private val database: LocalDatabase, private val service: ArcgisApiService, private val sharedPreferences: SharedPreferences, private val dateProvider: DateProvider): CountryRepositoryImpl{
    val country: LiveData<List<CountryModel>> = Transformations.map(database.databaseDao.getCountryFromDatabase()){
        it.asRepositoryDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun refreshData(){
        withContext(Dispatchers.IO){
            val dataList = service.getArcgisData().await()
            database.databaseDao.insertCountryToDatabase(*dataList.asDatabaseModel())
            setCacheTimeData(dateProvider.now().toEpochSecond())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isCacheExpiredData(): Boolean {
        val currentTime = dateProvider.now().toEpochSecond()
        val expirationTime = (EXPIRY_TIME_DATA).toLong()
        val lastCacheTime = sharedPreferences.getLong(DATA_CACHE_TIME, 0)
        return currentTime - lastCacheTime > expirationTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCacheTimeData(time: Long){
        sharedPreferences.edit {
            putLong(DATA_CACHE_TIME, time)
        }
    }

    companion object {
        const val DATA_CACHE_TIME = "data_cache_time"
    }
}