package com.example.devexercise.repository.impl

import androidx.lifecycle.LiveData
import com.example.devexercise.repository.MapPointModel

interface MapRepositoryImpl {
    suspend fun refreshData()
    fun getPointDetails(pointId: Long): LiveData<List<MapPointModel>>
}