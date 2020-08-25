package com.example.devexercise.network

import com.example.devexercise.database.MapPointEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkMapDataContainer(
    @field:Json(name = "features")
    val pointsContainer: List<PointsContainer>
)

@JsonClass(generateAdapter = true)
data class PointsContainer(
    @field:Json(name = "attributes")
    val pointDetails: PointDetails
)

@JsonClass(generateAdapter = true)
data class PointDetails(
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

fun NetworkMapDataContainer.asDatabaseModel(): Array<MapPointEntity>{
    return pointsContainer.map{
        MapPointEntity(
            OBJECTID = it.pointDetails.OBJECTID,
            Province_State = it.pointDetails.Province_State,
            Country_Region = it.pointDetails.Country_Region,
            Last_Update = it.pointDetails.Last_Update,
            Lat = it.pointDetails.Lat,
            Long_ = it.pointDetails.Long_,
            Confirmed = it.pointDetails.Confirmed,
            Recovered = it.pointDetails.Recovered,
            Deaths = it.pointDetails.Deaths,
            Active = it.pointDetails.Active
        )
    }.toTypedArray()
}