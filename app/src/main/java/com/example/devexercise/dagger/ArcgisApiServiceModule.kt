package com.example.devexercise.dagger

import com.example.devexercise.network.ArcgisApiService
import com.example.devexercise.network.ArcgisMapService
import com.example.devexercise.network.CASES_LAYER
import com.example.devexercise.network.COUNTRY_LAYER
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class ArcgisApiServiceModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
                                .add(KotlinJsonAdapterFactory())
                                .build()

    @Provides
    @Singleton
    fun provideArcgisApiService(moshi: Moshi): ArcgisApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(COUNTRY_LAYER)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(ArcgisApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideArcgisMapService(moshi: Moshi): ArcgisMapService {
        val retrofit = Retrofit.Builder()
            .baseUrl(CASES_LAYER)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(ArcgisMapService::class.java)
    }
}