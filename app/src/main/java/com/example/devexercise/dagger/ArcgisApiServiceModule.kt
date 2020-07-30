package com.example.devexercise.dagger

import com.example.devexercise.network.ArcgisApiService
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
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(COUNTRY_LAYER)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit): ArcgisApiService = retrofit.create(ArcgisApiService::class.java)
}