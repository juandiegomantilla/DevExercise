package com.example.devexercise.database

import android.content.SharedPreferences
import com.example.devexercise.repository.LoggedUser
import javax.inject.Inject

class LoginLocalDataSource @Inject constructor(private val prefs: SharedPreferences){
    var user: LoggedUser?
        get() {
            val username = prefs.getString(KEY_USER_NAME, null) ?: return null
            val pass = prefs.getString(KEY_PASS, null) ?: return null
            val displayName = prefs.getString(KEY_DISPLAY_NAME, null) ?: return null
            val license = prefs.getString(KEY_LICENSE, null) ?: return null
            return LoggedUser(userId = username, pass = pass, displayName = displayName, license = license)
        }
        set(value) {
            if(value != null){
                val editor = prefs.edit()
                editor.putString(KEY_USER_NAME, value.userId)
                editor.putString(KEY_PASS, value.pass)
                editor.putString(KEY_DISPLAY_NAME, value.displayName)
                editor.putString(KEY_LICENSE, value.license)

                editor.putString(STORED_USER, value.userId)
                editor.putString(STORED_PASS, value.pass)

                editor.apply()
            }
        }

    fun logout(){
        val editor = prefs.edit()
        editor.putString(KEY_USER_NAME, null)
        editor.putString(KEY_PASS, null)
        editor.putString(KEY_DISPLAY_NAME, null)
        editor.putString(KEY_LICENSE, null)
        editor.apply()
        editor.clear()
    }

    fun storedUser(): List<String?>{
        val user: String? = prefs.getString(STORED_USER, null)
        val pass: String? = prefs.getString(STORED_PASS, null)
        return listOf(user, pass)
    }

    fun clearStoredCredentials(){
        val editor = prefs.edit()
        editor.putString(STORED_USER, null)
        editor.putString(STORED_PASS, null)
        editor.apply()
        editor.clear()
    }

    companion object {
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_PASS = "KEY_PASS"
        private const val KEY_LICENSE = "KEY_LICENSE"
        private const val KEY_DISPLAY_NAME = "KEY_DISPLAY_NAME"

        private const val STORED_USER = "STORED_USER"
        private const val STORED_PASS = "STORED_PASS"

        @Volatile
        private var INSTANCE: LoginLocalDataSource? = null

        fun getInstance(sharedPreferences: SharedPreferences): LoginLocalDataSource {
            return INSTANCE ?: LoginLocalDataSource(sharedPreferences).also {
                INSTANCE = it
            }
        }
    }
}