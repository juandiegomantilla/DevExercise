package com.example.devexercise.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.databinding.ActivityLoginBinding
import com.example.devexercise.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: LoginViewModel

    private lateinit var binding: ActivityLoginBinding

    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        (applicationContext as DevExerciseApp).appComp().inject(this)

        val sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)

        binding.viewModel = viewModel

        binding.inputUsername.addTextChangedListener(loginTextWatcher)
        binding.inputPassword.addTextChangedListener(loginTextWatcher)

        binding.loginButton.setOnClickListener {
            try{
                val username = input_username.text.toString()
                val password = input_password.text.toString()
                val remember = binding.rememberMeSwitch.isChecked
                //val username = "boooo"
                //val password = "fuuuu"

                val status = viewModel.login(username, password, remember)

                status.observe(this, Observer { loginStatus ->
                    //println("Status: $loginStatus")
                    when(loginStatus){
                        "Success" -> goToHomeScreen()
                        "Not_Success" -> Snackbar.make(it, "Incorrect Credentials", Snackbar.LENGTH_LONG).show()
                        "Not_Started" -> Snackbar.make(it, "Connection Error", Snackbar.LENGTH_LONG).show()
                    }
                })
            }catch(e: Exception){
                println(e)
            }
        }

        binding.rememberMeSwitch.setOnCheckedChangeListener { button: CompoundButton, isChecked: Boolean ->
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
}