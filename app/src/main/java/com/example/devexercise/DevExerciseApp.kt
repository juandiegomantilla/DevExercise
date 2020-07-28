package com.example.devexercise

import android.app.Application
import com.example.devexercise.network.ArcgisAuthentication

class DevExerciseApp: Application(){
    override fun onCreate() {
        super.onCreate()

        ArcgisAuthentication.setLicence()
    }
}