package com.example.devexercise.repository

import androidx.lifecycle.*
import com.example.devexercise.database.LoginLocalDataSource
import com.example.devexercise.network.LoginRemoteDataSource
import com.example.devexercise.repository.impl.*
import javax.inject.Inject

class LoginRepository @Inject constructor(private val localDataSource: LoginLocalDataSource, private val remoteDataSource: LoginRemoteDataSource):
Logout, Login, StoreUser, SetUser, UserIsRemembered {
    var user: LoggedUser? = null
        private set

    var rememberActive = false

    val isLoggedIn: Boolean
        get() = user != null

    val userInfo = storeUserInfo()

    init {
        user = localDataSource.user
    }

    override fun logout(){
        user = null
        localDataSource.logout()
    }

    override fun login(username: String, password: String, remember: Boolean): LiveData<String> {

        remoteDataSource.login(username, password)

        storeUserInfo()

        rememberActive = remember

        return remoteDataSource.status
    }

    override fun storeUserInfo(): LiveData<LoggedUser> {
        val userInfo = remoteDataSource.userInfo
        val nameObserver = Observer<LoggedUser> {
            setLoggedInUser(it)
        }
        userInfo.observeForever(nameObserver)
        return userInfo
    }

    override fun setLoggedInUser(loggedInUser: LoggedUser){
        user = loggedInUser
        localDataSource.user = user
    }

    override fun userRemembered(valid: Boolean) {
        rememberActive = valid
    }
}