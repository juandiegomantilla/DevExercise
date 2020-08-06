package com.example.devexercise.network

import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.layers.FeatureLayer
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

//BASE URL (For testing JSON in the web browser): https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/2/query?where=1%3D1&outFields=*&f=json&token=4GADju5LDe1_MnzQ1qWnR1slzI8OcZCmu-Win-FaPDiD7AQvaOcY2hVh6B9V21PZY5mJAXEaQLLX5cLbvOlv7CwnhYH2sNOq2VLgKRk-pySPEfSGdZaG-co5hgf3FigpLUueHjL34ALgJjUdDERWFbTVgqfdLQjY98hQP0d-jZEdVv-MFjsknFMpyI9R1-dw4AOMvFtlRbjKdz9IHZwH7IKKtaKfAJWYQcPz-e35SI8

//Arcgis REST Service Url's
private const val DEATH_LAYER = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/0/"
const val CASES_LAYER = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/1/"
const val COUNTRY_LAYER = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/Coronavirus_2019_nCoV_Cases/FeatureServer/2/"

//QUERIES
private const val QUERY_COUNTRY_LAYER = "query?where=1%3D1&outFields=*&f=json"
private const val QUERY_CASES_LAYER = "query?where=1%3D1&outFields=OBJECTID%2CProvince_State%2CCountry_Region%2CLast_Update%2CLat%2CLong_%2CConfirmed%2CRecovered%2CDeaths%2CActive&returnGeometry=false&f=pjson"


//Cases by Country Query:
interface ArcgisApiService  {
    @GET(QUERY_COUNTRY_LAYER)
    fun getArcgisData(): Deferred<NetworkDataContainer>
}

//Map Points Query:
interface ArcgisMapService  {
    @GET(QUERY_CASES_LAYER)
    fun getArcgisMapData(): Deferred<NetworkMapDataContainer>
}

//Map Layers retrieving:
object ArcgisLayer{
    private val deathLayerTable = ServiceFeatureTable(DEATH_LAYER)
    val casesLayerTable = ServiceFeatureTable(CASES_LAYER)

    val deathLayer = FeatureLayer(deathLayerTable)
    val casesLayer = FeatureLayer(casesLayerTable)
}
