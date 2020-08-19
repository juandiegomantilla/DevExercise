package com.example.devexercise.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import com.example.devexercise.network.Results
import com.example.devexercise.repository.LoginRepository
import kotlinx.coroutines.*
import javax.inject.Inject

//class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository): ViewModel(){
class LoginViewModel(private var sharedPreferences: SharedPreferences): ViewModel(){

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var username = ""
    var password = ""

    private var remoteDataSource = LoginRemoteDataSource()
    private var localDataSource = LoginLocalDataSource.getInstance(sharedPreferences)
    private var loginRepository = LoginRepository(localDataSource, remoteDataSource)


    fun login(username: String, password: String): LiveData<String> {
        return loginRepository.login(username, password)
    }
}