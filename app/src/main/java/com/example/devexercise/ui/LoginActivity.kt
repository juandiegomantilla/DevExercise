package com.example.devexercise.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.devexercise.R
import com.example.devexercise.databinding.ActivityLoginBinding
import com.example.devexercise.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private lateinit var binding: ActivityLoginBinding

    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.viewModel = viewModel

        binding.inputUsername.addTextChangedListener(loginTextWatcher)
        binding.inputPassword.addTextChangedListener(loginTextWatcher)

        binding.loginButton.setOnClickListener {
            try{
                val username = input_username.text.toString()
                val password = input_password.text.toString()
                val remember = binding.rememberMeSwitch.isChecked

                val status = viewModel.login(username, password, remember)

                status.observe(this, Observer { loginStatus ->
                    when(loginStatus){
                        "Success" -> goToHomeScreen()
                        "Not_Success" -> Snackbar.make(it, "Incorrect Credentials", Snackbar.LENGTH_LONG).show()
                        "Not_Connected" -> Snackbar.make(it, "Not Connected to the Internet", Snackbar.LENGTH_LONG).show()
                    }
                })
            }catch(e: Exception){
                println(e)
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

    private val loginTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val userInput = binding.inputUsername.text.toString().trim()
            val passInput = binding.inputPassword.text.toString().trim()

            binding.loginButton.isEnabled = (userInput.isNotEmpty() && passInput.isNotEmpty())
            binding.rememberMeSwitch.isEnabled = (userInput.isNotEmpty() && passInput.isNotEmpty())
        }
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}