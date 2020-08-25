package com.example.devexercise.dagger

import android.app.Application
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.ui.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ActivityCreatorModule::class, AppModule::class])
interface AppComponent{
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
    fun inject(app: DevExerciseApp)
}