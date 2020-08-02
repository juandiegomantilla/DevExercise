package com.example.devexercise.viewmodel.impl

import com.esri.arcgisruntime.mapping.ArcGISMap
import com.example.devexercise.repository.CountryModel

interface CountryMapViewModelImpl {
    fun createMap(country: CountryModel): ArcGISMap
}