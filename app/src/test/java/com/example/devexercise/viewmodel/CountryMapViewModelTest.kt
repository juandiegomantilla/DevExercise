package com.example.devexercise.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.devexercise.repository.CountryModel
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CountryMapViewModelTest {

    @Mock
    lateinit var country: CountryModel

    lateinit var countryMapViewModel: CountryMapViewModel

    @Before
    fun setUp() {
        country = CountryModel(
            OBJECTID = 1,
            Country_Region = "Australia",
            Last_Update = "1595208913000",
            Lat = -25.0,
            Long_ = 133.0,
            //Lat = null,
            //Long_ = null,
            Confirmed = 12069,
            Deaths = 123,
            Recovered = 8444,
            Active = 3502
        )

        countryMapViewModel = mock(CountryMapViewModel::class.java)
    }

    @Test
    fun shouldReturnArGISMap_whenCalled_createMap() {
         countryMapViewModel.createMap(country)
    }
}