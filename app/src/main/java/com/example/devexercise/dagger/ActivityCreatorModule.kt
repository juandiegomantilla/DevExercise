package com.example.devexercise.dagger

import com.example.devexercise.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityCreatorModule{
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}