package com.example.devexercise.repository.impl

import androidx.lifecycle.LiveData
import com.example.devexercise.repository.LoggedUser

interface Logout {
    fun logout()
}

interface Login {
    fun login(username: String, password: String, remember: Boolean): LiveData<String>
}

interface StoreUser {
    fun storeUserInfo(): LiveData<LoggedUser>
}

interface SetUser {
    fun setLoggedInUser(loggedInUser: LoggedUser)
}

interface UserIsRemembered {
    fun userRemembered(valid: Boolean)
}