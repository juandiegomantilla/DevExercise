package com.example.devexercise.dagger

import com.example.devexercise.ui.CountryMapFragment
import com.example.devexercise.ui.HomeFragment
import com.example.devexercise.ui.MapFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HomeFragmentModule{
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeCountryMapFragment(): CountryMapFragment
}