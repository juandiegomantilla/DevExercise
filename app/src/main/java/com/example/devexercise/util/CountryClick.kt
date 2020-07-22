package com.example.devexercise.util

import com.example.devexercise.repository.CountryModel

class CountryClick(val clickListener: (CountryModel) -> Unit){
    fun onClick(country: CountryModel) = clickListener(country)
}