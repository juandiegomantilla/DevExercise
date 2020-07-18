package com.example.devexercise.network
import com.example.devexercise.database.CountryEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkDataContainer(
    @field:Json(name = "features")
    val countryContainer: List<CountryContainer>
)

@JsonClass(generateAdapter = true)
data class CountryContainer(
    @field:Json(name = "attributes")
    val countryDetails: CountryDetails
)

@JsonClass(generateAdapter = true)
data class CountryDetails(
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

fun NetworkDataContainer.asDatabaseModel(): Array<CountryEntity>{
    //println("asDatabaseModel:" + casesCountry.get(0).countryInfo.Country_Region)
    //println("asDatabaseModel:" + casesCountry)
    return countryContainer.map{
        CountryEntity(
            OBJECTID = it.countryDetails.OBJECTID,
            Country_Region = it.countryDetails.Country_Region,
            Last_Update = it.countryDetails.Last_Update,
            Lat = it.countryDetails.Lat,
            Long_ = it.countryDetails.Long_,
            Confirmed = it.countryDetails.Confirmed,
            Deaths = it.countryDetails.Deaths,
            Recovered = it.countryDetails.Recovered,
            Active = it.countryDetails.Active
        )
    }.toTypedArray()
}
