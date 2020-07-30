package com.example.devexercise

import android.app.Application
import com.example.devexercise.dagger.AppComponent
import com.example.devexercise.dagger.AppModule
import com.example.devexercise.dagger.DaggerAppComponent
import com.example.devexercise.network.ArcgisAuthentication

class DevExerciseApp: Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = initDagger(this)

        ArcgisAuthentication.setLicence("jmantilla","unclesam123")
    }

    private fun initDagger(app: DevExerciseApp): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()

    fun appComp() = appComponent
}