package com.example.devexercise.viewmodel

import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    lateinit var mapViewModel: MapViewModel

    @Before
    fun setUp() {
        mapViewModel = Mockito.mock(MapViewModel::class.java)
    }

    @Test
    fun shouldLoadLayersAndReturnNothing_whenCalled_loadLayers() {
        //doNothing().`when`(mapViewModel).loadMap()
    }
}