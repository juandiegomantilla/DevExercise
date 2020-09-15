package com.example.devexercise.repository.impl

interface RefreshData {
    suspend fun refreshData()
}

interface IsCacheExpiredData {
    fun isCacheExpiredData(): Boolean
}

interface SetCacheTimeData {
    fun setCacheTimeData(time: Long)
}