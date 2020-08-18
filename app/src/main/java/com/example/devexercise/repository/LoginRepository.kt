package com.example.devexercise.repository

import androidx.lifecycle.*
import com.esri.arcgisruntime.loadable.LoadStatus
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource

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

    fun login(username: String, password: String): LiveData<LoadStatus> {

        remoteDataSource.login(username, password)

        val userInfo = remoteDataSource.userInfo

        val nameObserver = Observer<LoggedUser> {
            setLoggedInUser(it)
        }

        userInfo.observeForever(nameObserver)

        return remoteDataSource.status
    }

    fun setLoggedInUser(loggedInUser: LoggedUser){
        user = loggedInUser
        localDataSource.user = user
    }
}