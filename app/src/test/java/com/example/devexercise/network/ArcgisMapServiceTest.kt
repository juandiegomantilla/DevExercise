package com.example.devexercise.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(MockitoJUnitRunner::class)
class ArcgisMapServiceTest {

    @Mock
    lateinit var mapServiceIntercepted: ArcgisMapService

    @Mock
    lateinit var mapServiceArcgis: ArcgisMapService

    @Before
    fun setup() {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        //Retrofit with interceptor
        val retrofitWithInterceptor = Retrofit.Builder()
            .baseUrl(CASES_LAYER)
            .client(OkHttpClient.Builder().addInterceptor(MockMapInterceptor()).build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        mapServiceIntercepted = retrofitWithInterceptor.create(ArcgisMapService::class.java)

        //Retrofit without interceptor
        val retrofitArcgis = Retrofit.Builder()
            .baseUrl(CASES_LAYER)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        mapServiceArcgis = retrofitArcgis.create(ArcgisMapService::class.java)
    }

    @Test
    fun `Data Should Be Received From The Arcgis Server`(){
        runBlocking {
            val mapData = mapServiceArcgis.getArcgisMapData().await()
            val mapDataCount = mapData.pointsContainer.count()
            println("Arcgis server map data: $mapData")
            println("Map data items count: $mapDataCount")
            assert(mapDataCount >= 740)
        }
    }

    @Test
    fun `Map Data Should Be Received From The MockMapInterceptor`(){
        runBlocking {
            val interceptedMapData = mapServiceIntercepted.getArcgisMapData().await()
            val interceptedMapDataCount = interceptedMapData.pointsContainer.count()
            println("Map interceptor data: $interceptedMapData")
            println("Map interceptor items count: $interceptedMapDataCount")
            assert(interceptedMapDataCount == 250)
        }
    }

    @Test
    fun `Arcgis Map Data And Intercepted Map Data Must Be Different`(){
        runBlocking {
            val arcgisMapData = mapServiceArcgis.getArcgisMapData().await()
            val interceptedMapData = mapServiceIntercepted.getArcgisMapData().await()
            assert(arcgisMapData != interceptedMapData)
        }
    }
}