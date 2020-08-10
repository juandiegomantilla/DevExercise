package com.example.devexercise.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.security.AuthenticationManager
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler
import com.esri.arcgisruntime.security.OAuthConfiguration
import com.example.devexercise.R
import com.example.devexercise.network.ArcgisAuthentication
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.net.MalformedURLException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            try{
                ArcgisAuthentication.setLicence(input_username.text.toString(),input_password.text.toString())
                val homeIntent = Intent(this, MainActivity::class.java)
                startActivity(homeIntent)
                finish()
            }catch(e: Exception){
                println(e)
            }
        }
        /*try{
            val oAuthConfiguration = OAuthConfiguration(
                "https://www.arcgis.com/",
                "oG1njhZYp2tT9D5y",
                "urn:ietf:wg:oauth:2.0:oob"
            )

            val defaultAuthenticationChallengeHandler = DefaultAuthenticationChallengeHandler(this)

            AuthenticationManager.setAuthenticationChallengeHandler(defaultAuthenticationChallengeHandler)

            AuthenticationManager.addOAuthConfiguration(oAuthConfiguration)

        }catch(e: MalformedURLException){
            println("Error in OAuthConfiguration URL: "+ e.message)
        }*/
    }
}
