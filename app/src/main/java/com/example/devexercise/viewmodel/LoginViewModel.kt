package com.example.devexercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.devexercise.repository.LoggedUser
import com.example.devexercise.repository.LoginRepository
import kotlinx.coroutines.*
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository): ViewModel(){

    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var username = ""
    var password = ""

    fun login(username: String, password: String, remember: Boolean): LiveData<String> {
        return loginRepository.login(username, password, remember)
    }

    fun getUserInfo(): LiveData<LoggedUser>{
        return loginRepository.userInfo
    }

    fun logout(){
        loginRepository.logout()
    }

    fun rememberAction(){
        if(loginRepository.rememberActive) {
            println("Remembered")
        }else{
            loginRepository.logout()
            println("Not Remembered")
        }
    }
}