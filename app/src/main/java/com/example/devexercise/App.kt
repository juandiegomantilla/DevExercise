package com.example.devexercise

import android.app.Application
import com.example.devexercise.dagger.AppComponent
import com.example.devexercise.dagger.AppModule
import com.example.devexercise.dagger.DaggerAppComponent

class App: Application(){

    //private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        //appComponent = DaggerAppComponent.builder().application(AppModule()).build()
    }
    //fun getComponent() = appComponent
}