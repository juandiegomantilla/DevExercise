package com.example.devexercise.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.Observer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.UserCredential
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    //@Inject
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //(applicationContext as DevExerciseApp).appComp().inject(this)

        val sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)

        viewModel = LoginViewModel(sharedPreferences)

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            try{
                //viewModel.username = "jmantilla"
                //viewModel.password = "unclesam123"

                val status = viewModel.login("jmantilla", "unclesam123")

                status.observe(this, Observer { loginStatus ->
                    println("LOGIN STATUS!! ${loginStatus.name}")
                    if(loginStatus.name == "LOADED"){
                        val homeIntent = Intent(this, MainActivity::class.java)
                        startActivity(homeIntent)
                        finish()
                    }
                })
            }catch(e: Exception){
                println(e)
            }
        }
    }
}
