package com.example.devexercise.network.connection

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import javax.inject.Inject

class ConnectionLiveData @Inject constructor(private val application: Application): LiveData<Boolean>() {
    private val networkReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            postValue(context?.isConnected)
        }
    }

    override fun onActive() {
        super.onActive()
        application.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onInactive() {
        super.onInactive()
        try {
            application.unregisterReceiver(networkReceiver)
        }catch (e: Exception){
            println("Listener error: $e")
        }
    }
}

val Context.isConnected: Boolean
    get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected == true