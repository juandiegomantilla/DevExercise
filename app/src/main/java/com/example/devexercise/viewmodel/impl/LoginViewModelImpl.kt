package com.example.devexercise.viewmodel.impl

import androidx.lifecycle.LiveData
import com.example.devexercise.repository.LoggedUser

interface LoginViewModelImpl {
    fun login(username: String, password: String, remember: Boolean): LiveData<String>
    fun getUserInfo(): LiveData<LoggedUser>
    fun getDataStored(): LoggedUser?
    fun logout()
    fun rememberAction()
}