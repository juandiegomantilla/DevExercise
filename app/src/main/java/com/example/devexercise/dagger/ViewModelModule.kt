package com.example.devexercise.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.viewmodel.CountryMapViewModel
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

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
    @ViewModelKey(CountryMapViewModel::class)
    abstract fun bindCountryMapViewModel(mapViewModel: CountryMapViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(devExerciseAppViewModelFactory: DevExerciseAppViewModelFactory): ViewModelProvider.Factory
}