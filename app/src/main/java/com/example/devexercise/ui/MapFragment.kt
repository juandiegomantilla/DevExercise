package com.example.devexercise.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.esri.arcgisruntime.mapping.view.MapView
import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentMapBinding
import com.example.devexercise.viewmodel.MapViewModel
import com.example.devexercise.viewmodel.MapViewModelFactory
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment() {

    private val viewModel: MapViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "Unable to access the ViewModel"
        }
        ViewModelProviders.of(this, MapViewModelFactory(activity.application))
            .get(MapViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        binding.root.findViewById<MapView>(R.id.mapView).apply {
            map = viewModel.map
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.dispose()
    }
}