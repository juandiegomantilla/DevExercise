package com.example.devexercise.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.repository.CountryRepository
import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.repository.MapRepository
import com.example.devexercise.viewmodel.CountryMapViewModel
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.LoginViewModel
import com.example.devexercise.viewmodel.MapViewModel
import com.example.devexercise.viewmodel.impl.HomeViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindMapViewModel(mapViewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CountryMapViewModel::class)
    abstract fun bindCountryMapViewModel(mapViewModel: CountryMapViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(devExerciseAppViewModelFactory: DevExerciseAppViewModelFactory): ViewModelProvider.Factory
}