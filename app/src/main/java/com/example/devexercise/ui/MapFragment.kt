package com.example.devexercise.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentMapBinding
import com.example.devexercise.network.ArcgisLayer
import com.example.devexercise.util.MapPointAdapter
import com.example.devexercise.util.PointClick
import com.example.devexercise.viewmodel.MapViewModel
import com.example.devexercise.viewmodel.MapViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_point_details.view.*
import kotlin.math.roundToInt

class MapFragment : Fragment() {

    private val viewModel: MapViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "Unable to access the ViewModel"
        }
        ViewModelProviders.of(this, MapViewModelFactory(activity.application))
            .get(MapViewModel::class.java)
    }

    private var viewModelAdapter: MapPointAdapter? = null

    private var pointPopup: PopupWindow? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        //val window = PopupWindow(context)
        //val view = layoutInflater.inflate(R.layout.fragment_point_details, null)
        //window.contentView = view
        //window.showAtLocation(mapView, Gravity.CENTER, 0, 0)
        /*val calloutContent = binding.root.findViewById<RecyclerView>(R.id.point_recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }*/

        val calloutContent = TextView(context).apply {
            setTextColor(Color.BLACK)
            isSingleLine = false
            isVerticalScrollBarEnabled = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
            movementMethod = ScrollingMovementMethod()
            setLines(1)
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
                    val featureQueryResultFuture = viewModel.getFeatureQueryResult(envelope)
                    featureQueryResultFuture.addDoneListener {
                        try {
                            val featureQueryResult = featureQueryResultFuture.get()
                            val iterator = featureQueryResult.iterator()
                            var counter = 0
                            while (iterator.hasNext()) {
                                val feature = iterator.next()
                                val attr = feature.attributes
                                val pointId = attr["OBJECTID"] as Long
                                val pointRequested = viewModel.mapDataList(pointId)

                                pointRequested.observe(viewLifecycleOwner, Observer { point ->
                                    point?.apply {
                                        viewModelAdapter?.pointDetails = point
                                        println(point)
                                    }
                                })

                                pointPopup = showPointDetails()
                                pointPopup?.isOutsideTouchable = true
                                pointPopup?.isFocusable = true
                                pointPopup?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                pointPopup?.showAtLocation(it, Gravity.CENTER, 0, 0)

                                calloutContent.text = pointId.toString()
                                counter++
                                it.callout.apply {
                                    location = tappedPoint
                                    content = calloutContent
                                    show()
                                }
                            }
                        } catch (e: Exception) {
                            println(e)
                            Snackbar.make(activity!!.findViewById(android.R.id.content), "Select map point failed: " + e.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    return super.onSingleTapConfirmed(motionEvent)
                }
            }
        }

        return binding.root
    }

    private fun showPointDetails(): PopupWindow{
        viewModelAdapter = MapPointAdapter(PointClick{/*HOLAAA*/})
        val inflater = layoutInflater.inflate(R.layout.fragment_point_details, null, false)
        val recyclerView = inflater.findViewById<RecyclerView>(R.id.point_recycler_view)
        //if(recyclerView.parent != null) recyclerView.removeView(recyclerView)
        recyclerView.adapter = viewModelAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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