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
class ArcgisApiServiceTest {

    @Mock
    lateinit var serviceIntercepted: ArcgisApiService

    @Mock
    lateinit var serviceArcgis: ArcgisApiService

    @Before
    fun setup() {

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        //Retrofit with interceptor
        val retrofitWithInterceptor = Retrofit.Builder()
            .baseUrl(COUNTRY_LAYER)
            .client(OkHttpClient.Builder().addInterceptor(MockApiInterceptor()).build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        serviceIntercepted = retrofitWithInterceptor.create(ArcgisApiService::class.java)

        //Retrofit without interceptor
        val retrofitArcgis = Retrofit.Builder()
            .baseUrl(COUNTRY_LAYER)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        serviceArcgis = retrofitArcgis.create(ArcgisApiService::class.java)
    }

    @Test
    fun `Data Should Be Received From The Arcgis Server`(){
        runBlocking {
            val data = serviceArcgis.getArcgisData().await()
            println("Arcgis server: $data")
            assert(data.countryContainer.count() >= 180)
        }
    }

    @Test
    fun `Data Should Be Received From The MockApiInterceptor`(){
        runBlocking {
            val interceptedData = serviceIntercepted.getArcgisData().await()
            println("Interceptor: $interceptedData")
            assert(interceptedData.countryContainer.count() == 188)
        }
    }

    @Test
    fun `Arcgis Data And Intercepted Data Must Be Different`(){
        runBlocking {
            val arcgisData = serviceArcgis.getArcgisData().await()
            val interceptedData = serviceIntercepted.getArcgisData().await()
            assert(arcgisData != interceptedData)
        }
    }
}