package com.example.devexercise.viewmodel.impl

import com.example.devexercise.repository.CountryModel

interface HomeViewModelImpl {
    fun presentData(): String
    fun deleteMapAreas()
    fun getData()
    fun displayCountryOnMap(country: CountryModel)
    fun displayCountryOnMapComplete()
    fun checkConnection()
}