package com.example.devexercise.dagger

import androidx.fragment.app.FragmentFactory
import com.example.devexercise.ui.CountryMapFragment
import com.example.devexercise.ui.HomeFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class HomeFragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeCountryMapFragment(): CountryMapFragment

    @Binds
    abstract fun bindFragmentFactory(devExerciseFragmentFactory: DevExerciseFragmentFactory): FragmentFactory
}