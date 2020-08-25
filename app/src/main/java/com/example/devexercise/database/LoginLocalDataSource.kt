package com.example.devexercise.database

import android.content.SharedPreferences
import com.example.devexercise.repository.LoggedUser
import javax.inject.Inject

class LoginLocalDataSource @Inject constructor(private val prefs: SharedPreferences){
    var user: LoggedUser?
        get() {
            val username = prefs.getString(KEY_USER_NAME, null) ?: return null
            val displayName = prefs.getString(KEY_DISPLAY_NAME, null) ?: return null
            val license = prefs.getString(KEY_LICENSE, null) ?: return null
            return LoggedUser(userId = username, displayName = displayName, license = license)
        }
        set(value) {
            if(value != null){
                val editor = prefs.edit()
                editor.putString(KEY_USER_NAME, value.userId)
                editor.putString(KEY_DISPLAY_NAME, value.displayName)
                editor.putString(KEY_LICENSE, value.license)
                editor.apply()
            }
        }

    fun logout(){
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, null)
        editor.putString(KEY_DISPLAY_NAME, null)
        editor.putString(KEY_LICENSE, null)
        editor.apply()
        editor.clear()
    }

    companion object {
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_LICENSE = "KEY_LICENSE"
        private const val KEY_DISPLAY_NAME = "KEY_DISPLAY_NAME"

        @Volatile
        private var INSTANCE: LoginLocalDataSource? = null

        fun getInstance(sharedPreferences: SharedPreferences): LoginLocalDataSource {
            return INSTANCE ?: LoginLocalDataSource(sharedPreferences).also {
                INSTANCE = it
            }
        }
    }
}