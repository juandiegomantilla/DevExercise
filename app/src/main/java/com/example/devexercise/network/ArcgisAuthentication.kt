package com.example.devexercise.network

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.security.UserCredential

object ArcgisAuthentication {

    fun setLicence(){
        val credential = UserCredential("jmantilla", "unclesam123")
        val portal = Portal("https://www.arcgis.com")

        portal.credential = credential
        portal.loadAsync()

        portal.addDoneLoadingListener{
            if(portal.loadStatus == LoadStatus.LOADED){
                val licenseFuture = portal.fetchLicenseInfoAsync()
                licenseFuture.addDoneListener {
                    try {
                        val licenceInfo = licenseFuture.get()
                        val licenceJson = licenceInfo.toJson()
                        ArcGISRuntimeEnvironment.setLicense(licenceInfo)
                        println("ArcGIS Runtime Environment Successfully Licenced")
                    } catch (e: Exception){
                        println("Error: $e")
                    }
                }
            }
        }
    }

}