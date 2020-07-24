package com.example.devexercise.network

import com.example.devexercise.DevExerciseApp

class DevExerciseAppTest: DevExerciseApp(){
    var url = "http://127.0.0.1:8080"
    override fun getBaseUrl(): String {
        return url
    }
}