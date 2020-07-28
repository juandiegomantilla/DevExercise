package com.example.devexercise

import android.app.Application
import com.example.devexercise.dagger.AppComponent
import com.example.devexercise.dagger.AppModule
import com.example.devexercise.dagger.DaggerAppComponent
import com.example.devexercise.network.ArcgisAuthentication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App: Application(), HasAndroidInjector{

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    //private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        ArcgisAuthentication.setLicence()
        //appComponent = DaggerAppComponent.builder().application(AppModule()).build()
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}