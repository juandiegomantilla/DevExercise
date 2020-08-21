package com.example.devexercise.repository

import androidx.lifecycle.*
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import javax.inject.Inject

class LoginRepository @Inject constructor(private val localDataSource: LoginLocalDataSource, private val remoteDataSource: LoginRemoteDataSource){
    var user: LoggedUser? = null
        private set

    var rememberActive = false

    val isLoggedIn: Boolean
        get() = user != null

    lateinit var userInfo: LiveData<LoggedUser>

    init {
        user = localDataSource.user
        //logout()
    }

    fun logout(){
        user = null
        localDataSource.logout()
    }

    fun login(username: String, password: String, remember: Boolean): LiveData<String> {

        remoteDataSource.login(username, password, remember)

        userInfo = remoteDataSource.userInfo
        val nameObserver = Observer<LoggedUser> {
            setLoggedInUser(it)
        }
        userInfo.observeForever(nameObserver)

        rememberActive = remember

        return remoteDataSource.status
    }

    fun setLoggedInUser(loggedInUser: LoggedUser){
        user = loggedInUser
        localDataSource.user = user
    }
}