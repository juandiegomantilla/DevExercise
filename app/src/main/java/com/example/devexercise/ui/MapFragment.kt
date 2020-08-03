package com.example.devexercise.ui

import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentMapBinding
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.viewmodel.MapViewModel
import com.example.devexercise.viewmodel.MapViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import kotlin.math.roundToInt

class MapFragment : Fragment() {

    private val pointDetails = ArcgisLayer.casesLayerTable

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

        val calloutContent = TextView(context).apply {
            setTextColor(Color.BLACK)
            isSingleLine = false
            isVerticalScrollBarEnabled = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
            movementMethod = ScrollingMovementMethod()
            setLines(13)
        }

        binding.mapView.let {
            it.map = viewModel.map
            it.selectionProperties.color = Color.BLUE
            it.onTouchListener = object : DefaultMapViewOnTouchListener(context, it){
                override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {

                    if(it.callout.isShowing) {
                        it.callout.dismiss()
                        calloutContent.text = null
                    }

                    val tappedPoint = it.screenToLocation(android.graphics.Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt()))
                    val tolerance = 25
                    val mapTolerance = tolerance * it.unitsPerDensityIndependentPixel
                    val envelope = Envelope(tappedPoint.x - mapTolerance, tappedPoint.y - mapTolerance, tappedPoint.x + mapTolerance, tappedPoint.y + mapTolerance, it.spatialReference)
                    val queryParameters = QueryParameters()
                    queryParameters.geometry = envelope
                    ArcgisLayer.casesLayer.selectFeaturesAsync(queryParameters, FeatureLayer.SelectionMode.NEW)
                    val featureQueryResultFuture = pointDetails.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                    featureQueryResultFuture.addDoneListener {
                        try {
                            val featureQueryResult = featureQueryResultFuture.get()
                            val iterator = featureQueryResult.iterator()
                            var counter = 0
                            while (iterator.hasNext()) {
                                val feature = iterator.next()
                                val attr = feature.attributes
                                val keys = attr.keys
                                for(key in keys){
                                    val value = attr[key]
                                    calloutContent.append("$key|$value\n")
                                }
                                counter++
                                it.callout.apply {
                                    location = tappedPoint
                                    content = calloutContent
                                    show()
                                }
                            }
                        } catch (e: Exception) {
                            Snackbar.make(activity!!.findViewById(android.R.id.content), "Select feature failed: " + e.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    return super.onSingleTapConfirmed(motionEvent)
                }
            }
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