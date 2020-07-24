package com.example.devexercise

import android.app.Application

open class DevExerciseApp: Application(){
    open fun getBaseUrl() = "https://www.arcgis.com"
}