package com.example.devexercise.dagger

import com.example.devexercise.ui.CountryMapFragment
import com.example.devexercise.ui.HomeFragment
import com.example.devexercise.ui.LoginActivity
import com.example.devexercise.ui.MapFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, ViewModelModule::class, RepositoryModule::class, ArcgisApiServiceModule::class, LoginModule::class])
@Singleton
interface AppComponent{
    fun inject(target: HomeFragment)
    fun inject(target: MapFragment)
    fun inject(target: CountryMapFragment)
    fun inject(target: LoginActivity)
}
