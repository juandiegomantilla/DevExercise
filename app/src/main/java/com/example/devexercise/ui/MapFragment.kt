package com.example.devexercise.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentMapBinding
import com.example.devexercise.util.MapPointAdapter
import com.example.devexercise.viewmodel.MapViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import javax.inject.Inject
import kotlin.math.roundToInt

class MapFragment : Fragment() {

    @Inject
    lateinit var viewModel: MapViewModel

    private var viewModelAdapter: MapPointAdapter? = null
    override fun onAttach(context: Context) {
        (context.applicationContext as DevExerciseApp).appComp().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        viewModelAdapter = MapPointAdapter()

        binding.mapView.let {
            it.map = viewModel.map
            it.selectionProperties.color = Color.BLUE
            it.onTouchListener = object : DefaultMapViewOnTouchListener(context, it){
                override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {

                    val tappedPoint = it.screenToLocation(android.graphics.Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt()))
                    val tolerance = 25
                    val mapTolerance = tolerance * it.unitsPerDensityIndependentPixel
                    val envelope = Envelope(tappedPoint.x - mapTolerance, tappedPoint.y - mapTolerance, tappedPoint.x + mapTolerance, tappedPoint.y + mapTolerance, it.spatialReference)
                    val pointSelectedOnMap = viewModel.getPointOnMap(envelope)

                    pointSelectedOnMap.addDoneListener {
                        try {
                            val featureQueryResult = pointSelectedOnMap.get()
                            val iterator = featureQueryResult.iterator()
                            var counter = 0
                            while (iterator.hasNext()) {
                                val feature = iterator.next()
                                val attr = feature.attributes
                                val pointId = attr["OBJECTID"] as Long
                                val pointRequested = viewModel.getMapPointInfo(pointId)

                                pointRequested.observe(viewLifecycleOwner, Observer { point ->
                                    point?.apply {
                                        viewModelAdapter?.pointDetails = point
                                        //println(point)
                                    }
                                })

                                showPointDetails()

                                counter++
                            }
                        } catch (e: Exception) {
                            Snackbar.make(activity!!.findViewById(android.R.id.content), "Point selected failed: " + e.message, Snackbar.LENGTH_LONG).show()
                        }
                    }

                    return super.onSingleTapConfirmed(motionEvent)
                }
            }
        }

        return binding.root
    }

    private fun showPointDetails(){
        val dialog = Dialog(requireContext())
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.fragment_point_details)
        dialog.findViewById<RecyclerView>(R.id.point_recycler_view).apply {
            adapter = viewModelAdapter
            layoutManager = LinearLayoutManager(context)
        }
        if(dialog.isShowing){
            dialog.dismiss()
        }
        dialog.show()
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