package com.example.devexercise.database


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.repository.MapPointModel

@Entity
data class CountryEntity(
    @PrimaryKey
    val OBJECTID: Int,
    val Country_Region: String?,
    val Last_Update: String?,
    val Lat: Double?,
    val Long_: Double?,
    val Confirmed: Int?,
    val Deaths: Int?,
    val Recovered: Int?,
    val Active: Int?
)

@Entity
data class MapPointEntity(
    @PrimaryKey
    val OBJECTID: Long,
    val Province_State: String?,
    val Country_Region: String?,
    val Last_Update: String?,
    val Lat: Double?,
    val Long_: Double?,
    val Confirmed: Int?,
    val Recovered: Int?,
    val Deaths: Int?,
    val Active: Int?
)

fun List<CountryEntity>.asRepositoryDomainModel(): List<CountryModel>{
    return map {
        CountryModel(
            OBJECTID = it.OBJECTID,
            Country_Region = it.Country_Region,
            Last_Update = it.Last_Update,
            Lat = it.Lat,
            Long_ = it.Long_,
            Confirmed = it.Confirmed,
            Deaths = it.Deaths,
            Recovered = it.Recovered,
            Active = it.Active
        )
    }
}

fun List<MapPointEntity>.asMapRepositoryDomainModel(): List<MapPointModel>{
    return map {
        MapPointModel(
            OBJECTID = it.OBJECTID,
            Province_State = it.Province_State,
            Country_Region = it.Country_Region,
            Last_Update = it.Last_Update,
            Lat = it.Lat,
            Long_ = it.Long_,
            Confirmed = it.Confirmed,
            Recovered = it.Recovered,
            Deaths = it.Deaths,
            Active = it.Active
        )
    }
}

