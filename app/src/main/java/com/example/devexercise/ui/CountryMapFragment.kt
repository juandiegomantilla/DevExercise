package com.example.devexercise.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.esri.arcgisruntime.mapping.view.MapView

import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentCountryMapBinding
import com.example.devexercise.viewmodel.CountryMapViewModel
import com.example.devexercise.viewmodel.CountryMapViewModelFactory


class CountryMapFragment : Fragment() {

    private val viewModel: CountryMapViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "Unable to access the ViewModel"
        }
        val country = CountryMapFragmentArgs.fromBundle(arguments!!).selectedCountry
        ViewModelProviders.of(this, CountryMapViewModelFactory(country, activity.application)).get(CountryMapViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        val binding = FragmentCountryMapBinding.inflate(inflater)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        binding.root.findViewById<MapView>(R.id.mapView).apply {
            map = viewModel.countryMap
        }

        return binding.root
    }

}