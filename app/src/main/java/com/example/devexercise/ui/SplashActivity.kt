package com.example.devexercise.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.LicenseInfo
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.repository.LoginRepository
import com.example.devexercise.viewmodel.LoginViewModel
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var userLogin: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as DevExerciseApp).appComp().inject(this)
        setContentView(R.layout.activity_splash)
        checkUser()
    }

    private fun checkUser(){
        if(userLogin.isLoggedIn){
            val homeIntent = Intent(this, MainActivity::class.java)

            val storedLicense = userLogin.user?.license
            val licenseInfo = LicenseInfo.fromJson(storedLicense)
            ArcGISRuntimeEnvironment.setLicense(licenseInfo)

            startActivity(homeIntent)
            userLogin.userRemembered(true)
        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            userLogin.userRemembered(false)
        }
        finish()
    }
}
