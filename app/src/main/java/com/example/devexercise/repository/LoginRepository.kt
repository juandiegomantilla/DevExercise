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

    val userInfo = storeUserInfo()

    init {
        user = localDataSource.user
    }

    fun logout(){
        user = null
        localDataSource.logout()
    }

    fun login(username: String, password: String, remember: Boolean): LiveData<String> {

        remoteDataSource.login(username, password, remember)

        storeUserInfo()

        rememberActive = remember

        return remoteDataSource.status
    }

    private fun storeUserInfo(): LiveData<LoggedUser> {
        val userInfo = remoteDataSource.userInfo
        val nameObserver = Observer<LoggedUser> {
            setLoggedInUser(it)
        }
        userInfo.observeForever(nameObserver)
        return userInfo
    }

    fun setLoggedInUser(loggedInUser: LoggedUser){
        user = loggedInUser
        localDataSource.user = user
    }

    fun userRemembered(valid: Boolean) {
        rememberActive = valid
    }
}