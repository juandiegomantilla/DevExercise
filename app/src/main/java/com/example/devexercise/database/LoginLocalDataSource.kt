package com.example.devexercise.database

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.example.devexercise.repository.LoggedUser
import javax.inject.Inject

class LoginLocalDataSource (private val prefs: SharedPreferences){
    var user: LoggedUser?
        get() {
            val username = prefs.getString(KEY_USER_NAME, null) ?: return null
            return LoggedUser(userId = username, displayName = username)
        }
        set(value) {
            if(value != null){
                val editor = prefs.edit()
                editor.putString(KEY_USER_NAME, value.userId)
                editor.apply()
            }
        }

    fun logout(){
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, null)
        editor.apply()
    }

    companion object {
        private const val KEY_USER_NAME = "KEY_USER_NAME"

        @Volatile
        private var INSTANCE: LoginLocalDataSource? = null

        fun getInstance(sharedPreferences: SharedPreferences): LoginLocalDataSource {
            return INSTANCE ?: LoginLocalDataSource(sharedPreferences).also {
                INSTANCE = it
            }
        }
    }
}