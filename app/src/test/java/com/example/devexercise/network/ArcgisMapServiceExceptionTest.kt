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
class ArcgisMapServiceExceptionTest {

    @Mock
    lateinit var mapService: ArcgisMapService

    @Before
    fun setup() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(CASES_LAYER)
            .client(OkHttpClient.Builder().addInterceptor(MockMapInterceptor()).build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        mapService = retrofit.create(ArcgisMapService::class.java)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun `Throw HttpException when 404 occurs`(){
        runBlocking {
            mapService.getArcgisMapData().await()
        }
    }
}