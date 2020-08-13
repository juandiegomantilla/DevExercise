package com.example.devexercise.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.UserCredential
import com.example.devexercise.repository.LoggedUser
import kotlinx.coroutines.*

class LoginRemoteDataSource {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
    get() = _status

    fun login(username: String, password: String){

        _status.value = LoadStatus.NOT_LOADED

        val portal = Portal("https://www.arcgis.com")
        val cred = UserCredential(username, password)
        portal.credential = cred
        portal.loadAsync()

        portal.addDoneLoadingListener {
            if(portal.loadStatus == LoadStatus.LOADED){
                _status.value = LoadStatus.LOADED
                setLicense(portal)
            }
        }
    }

    private fun setLicense(portal: Portal) {
        val licenseFuture = portal.fetchLicenseInfoAsync()
        licenseFuture.addDoneListener {
            try {
                val licenceInfo = licenseFuture.get()
                val licenceJson = licenceInfo.toJson()
                ArcGISRuntimeEnvironment.setLicense(licenceInfo)
                println("JSON: $licenceJson")
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
}