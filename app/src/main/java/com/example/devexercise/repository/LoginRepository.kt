package com.example.devexercise.repository

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import com.example.devexercise.network.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository (private val localDataSource: LoginLocalDataSource, private val remoteDataSource: LoginRemoteDataSource){
    var user: LoggedUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = localDataSource.user
    }

    fun logout(){
        user = null
        localDataSource.logout()
    }

    fun setResults(user: String, name: String) = Results.Success(LoggedUser(user, name))

    fun login(username: String, password: String): LiveData<LoadStatus> {

        remoteDataSource.login(username, password)

        return remoteDataSource.status
    }

    fun setLoggedInUser(loggedInUser: LoggedUser){
        user = loggedInUser
        localDataSource.user = user
    }
}