package com.example.devexercise.viewmodel.impl

import com.example.devexercise.repository.CountryModel

interface HomeViewModelImpl {
    fun getData()
    fun displayCountryOnMap(country: CountryModel)
    fun displayCountryOnMapComplete()
}