package com.example.devexercise.dagger

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.devexercise.DevExerciseApp
import dagger.android.AndroidInjection
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection

object AppInjector {
    fun init(devExerciseApp: DevExerciseApp){
        DaggerAppComponent.builder().application(devExerciseApp)
            .build().inject(devExerciseApp)
        devExerciseApp.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) { }
            override fun onActivityResumed(activity: Activity) { }
            override fun onActivityPaused(activity: Activity) { }
            override fun onActivityStopped(activity: Activity) { }
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }
            override fun onActivityDestroyed(activity: Activity) { }
        })
    }

    private fun handleActivity(activity: Activity){
        if(activity is HasAndroidInjector){
            AndroidInjection.inject(activity)
        }
        if(activity is FragmentActivity){
            activity.supportFragmentManager
                .registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks(){
                        override fun onFragmentCreated(
                            fm: FragmentManager,
                            fragment: Fragment,
                            savedInstanceState: Bundle?
                        ) {
                            if(fragment is Injectable){
                                AndroidSupportInjection.inject(fragment)
                            }
                        }
                    }, true
                )
        }
    }
}