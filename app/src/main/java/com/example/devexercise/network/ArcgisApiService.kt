package com.example.devexercise.network

import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

//BASE URL (For testing JSON in the web browser): https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/2/query?where=1%3D1&outFields=*&f=json&token=4GADju5LDe1_MnzQ1qWnR1slzI8OcZCmu-Win-FaPDiD7AQvaOcY2hVh6B9V21PZY5mJAXEaQLLX5cLbvOlv7CwnhYH2sNOq2VLgKRk-pySPEfSGdZaG-co5hgf3FigpLUueHjL34ALgJjUdDERWFbTVgqfdLQjY98hQP0d-jZEdVv-MFjsknFMpyI9R1-dw4AOMvFtlRbjKdz9IHZwH7IKKtaKfAJWYQcPz-e35SI8

//Server JSON Url's and 'Cases by Country' query
private const val DEATH_LAYER = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/0/"
private const val CASES_LAYER = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/1/"
const val COUNTRY_LAYER = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/2/"
private const val QUERY_COUNTRY_LAYER = "query?where=1%3D1&outFields=*&f=json"


//Cases by Country Query:
interface ArcgisApiService  {
    @GET(QUERY_COUNTRY_LAYER)
    fun getArcgisData(): Deferred<NetworkDataContainer>
}

//Map layers retrieving:
object ArcgisLayer{
    private val deathLayerTable = ServiceFeatureTable(DEATH_LAYER)
    val casesLayerTable = ServiceFeatureTable(CASES_LAYER)

    val deathLayer = FeatureLayer(deathLayerTable)
    val casesLayer = FeatureLayer(casesLayerTable)
}

//query?where=1%3D1&outFields=OBJECTID%2CProvince_State%2CCountry_Region%2CLast_Update%2CLat%2CLong_%2CConfirmed%2CRecovered%2CDeaths%2CActive&returnGeometry=false&f=pjson
private const val QUERY_MAP_POINTS = "query?where=1%3D1&outFields=OBJECTID%2CProvince_State%2CCountry_Region%2CLast_Update%2CLat%2CLong_%2CConfirmed%2CRecovered%2CDeaths%2CActive&returnGeometry=false&f=pjson"

interface ArcgisMapService  {
    @GET(QUERY_MAP_POINTS)
    fun getArcgisMapData(): Deferred<NetworkMapDataContainer>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object ArcgisMapPoints{
    private val retrofit = Retrofit.Builder()
        .baseUrl(CASES_LAYER)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val retrofitMapService = retrofit.create(ArcgisMapService::class.java)
}

