package com.example.devexercise.network
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
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
class ArcgisApiServiceTest {

    @Mock
    lateinit var service: ArcgisApiService

    @Before
    fun setUp() {
        val retrofit = Retrofit.Builder()
            .baseUrl(COUNTRY_LAYER)
            .client(OkHttpClient.Builder().addInterceptor(MockInterceptor()).build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        service = retrofit.create(ArcgisApiService::class.java)
    }

    @Test
    fun `Data Should Be Received From The Arcgis Server`(){
        runBlocking {
            val data = ArcgisApi.retrofitService.getArcgisData().await()
            println(data)
        }
    }

    @Test
    fun `Data Should Be Received From The MockInterceptor`(){
        runBlocking {
            val interceptedData = service.getArcgisData().await()
            assert(interceptedData.countryContainer.count() == 188)
            println(interceptedData)
        }
    }

    @Test
    fun `Arcgis Data And Intercepted Data Must Be Different`(){
        runBlocking {
            val arcgisData = ArcgisApi.retrofitService.getArcgisData().await()
            val interceptedData = service.getArcgisData().await()
            assert(arcgisData != interceptedData)
        }
    }
}