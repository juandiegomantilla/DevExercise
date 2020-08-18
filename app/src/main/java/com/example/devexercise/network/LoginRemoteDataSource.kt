package com.example.devexercise.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.net.ConnectivityManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.UserCredential
import com.example.devexercise.repository.LoggedUser
import kotlinx.coroutines.*

class LoginRemoteDataSource {

    private val _userInfo = MutableLiveData<LoggedUser>()
    val userInfo: LiveData<LoggedUser>
        get() = _userInfo

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val portal = Portal("https://www.arcgis.com")
    private lateinit var cred: UserCredential

    fun login(username: String, password: String) {

        try{
            cred = UserCredential(username, password)

            if(portal.loadStatus == LoadStatus.NOT_LOADED){

                portal.credential = cred
                //println(portal.loadStatus)
                portal.loadAsync()
                //println(portal.loadStatus)

                portal.addDoneLoadingListener {
                    if(portal.loadStatus == LoadStatus.LOADED){
                        _status.value = portal.loadStatus
                        setLicense(portal)
                    }else{
                        _status.value = portal.loadStatus
                    }
                }
            } else {

                portal.credential = cred
                //println(portal.loadStatus)
                portal.retryLoadAsync()
                //println(portal.loadStatus)
            }
        }catch (parameterEx: IllegalArgumentException){
            _status.value = LoadStatus.NOT_LOADED
        }
    }

    private fun setLicense(portal: Portal) {
        val licenseFuture = portal.fetchLicenseInfoAsync()
        licenseFuture.addDoneListener {
            try {
                val licenceInfo = licenseFuture.get()
                val licenceJson = licenceInfo.toJson()
                ArcGISRuntimeEnvironment.setLicense(licenceInfo)

                _userInfo.value = LoggedUser(portal.user.username, portal.user.fullName, licenceJson)
                //println("JSON: $licenceJson")
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
}