package com.example.devexercise.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.UserCredential
import com.example.devexercise.network.connection.ConnectionLiveData
import com.example.devexercise.repository.LoggedUser
import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor(private val connectionLiveData: ConnectionLiveData) {

    private var isOnline = false

    init {
        checkConnection()
    }

    private val _userInfo = MutableLiveData<LoggedUser>()
    val userInfo: LiveData<LoggedUser>
        get() = _userInfo

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val portal = Portal("https://www.arcgis.com")
    private lateinit var cred: UserCredential

    fun login(username: String, password: String) {
        if(isOnline){
            cred = UserCredential(username, password)

            if(portal.loadStatus == LoadStatus.NOT_LOADED){

                portal.credential = cred
                portal.loadAsync()

                portal.addDoneLoadingListener {
                    if(portal.loadStatus == LoadStatus.LOADED){
                        _status.value = "Success"
                        setLicense(portal)
                    }else{
                        _status.value = "Not_Success"
                        _status.value = ""
                    }
                }
            } else {
                portal.credential = cred
                portal.retryLoadAsync()
            }
        }else{
            _status.value = "Not_Connected"
            _status.value = ""
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

            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }

    private fun checkConnection() {
        val connectionObserver = Observer<Boolean> {
            isOnline = it
        }
        connectionLiveData.observeForever(connectionObserver)
    }
}