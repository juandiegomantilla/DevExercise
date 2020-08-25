package com.example.devexercise.dagger

import com.example.devexercise.ui.LoginActivity
import com.example.devexercise.ui.MainActivity
import com.example.devexercise.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityCreatorModule {
    @ContributesAndroidInjector()
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector()
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}