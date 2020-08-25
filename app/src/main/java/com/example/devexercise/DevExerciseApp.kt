package com.example.devexercise

import android.app.Application
import com.example.devexercise.dagger.AppComponent
import com.example.devexercise.dagger.AppInjector
import com.example.devexercise.dagger.AppModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DevExerciseApp: Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)

    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}