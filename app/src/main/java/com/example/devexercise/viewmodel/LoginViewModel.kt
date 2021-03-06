package com.example.devexercise.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.devexercise.repository.LoggedUser
import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.viewmodel.impl.LoginViewModelImpl
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository): ViewModel(), LoginViewModelImpl{

    override fun login(username: String, password: String, remember: Boolean): LiveData<String> {
        return loginRepository.login(username, password, remember)
    }

    override fun getUserInfo(): LiveData<LoggedUser> = loginRepository.userInfo

    override fun getDataStored(): LoggedUser? = loginRepository.user

    override fun logout(){
        loginRepository.logout()
    }

    override fun rememberAction(){
        if(loginRepository.rememberActive) {
            println("Remembered")
        }else{
            loginRepository.logout()
            println("Not Remembered")
        }
    }
}