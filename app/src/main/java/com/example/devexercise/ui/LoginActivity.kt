package com.example.devexercise.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.UserCredential
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    //@Inject
    lateinit var viewModel: LoginViewModel
    lateinit var status: LiveData<LoadStatus>
    lateinit var sharedPreferences: SharedPreferences

    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //(applicationContext as DevExerciseApp).appComp().inject(this)

        sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)

        viewModel = LoginViewModel(sharedPreferences)

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            try{
                val username = input_username.text.toString()
                val password = input_password.text.toString()
                //val username = "boooo"
                //val password = "fuuuu"

                status = viewModel.login(username, password)

                status.observe(this, Observer { loginStatus ->
                    println("Status: ${loginStatus.name}")
                    when(loginStatus.name){
                        "LOADED" -> goToHomeScreen()
                        "FAILED_TO_LOAD" -> Snackbar.make(it, "Incorrect Credentials", Snackbar.LENGTH_LONG).show()
                        "NOT_LOADED" -> Snackbar.make(it, "Connection Error", Snackbar.LENGTH_LONG).show()
                    }
                })

            }catch(e: Exception){
                println(e)
            }
        }

        val rememberMeCheck: SwitchCompat = findViewById(R.id.remember_me_switch)
        rememberMeCheck.setOnCheckedChangeListener { button: CompoundButton, isChecked: Boolean ->
            if(isChecked){
                sharedPreferences.all.map { println(it.key + " - " + it.value) }
                println(sharedPreferences.all.values.count())
            } else {
                button?.isChecked = false
                println("not checked!")
            }
        }
    }

    private fun goToHomeScreen(){
        attempts++
        if(attempts == 1){
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        }
    }
}
