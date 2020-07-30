package com.example.devexercise.viewmodel.impl

import com.esri.arcgisruntime.mapping.ArcGISMap

interface MapViewModelImpl {
    fun createMap(): ArcGISMap
}