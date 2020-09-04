package com.example.devexercise.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.example.devexercise.R
import com.example.devexercise.dagger.Injectable
import com.example.devexercise.databinding.FragmentMapBinding
import com.example.devexercise.util.MapPointAdapter
import com.example.devexercise.viewmodel.CountryMapViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import javax.inject.Inject
import kotlin.math.roundToInt

class MapFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: CountryMapViewModel by viewModels { viewModelFactory }

    private var viewModelAdapter: MapPointAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        viewModelAdapter = MapPointAdapter()

        val downloadDialog = createProgressDialog()

        if(viewModel.tiledMap == null){
            binding.mapView.visibility = View.INVISIBLE
            binding.mapMessage.visibility = View.VISIBLE
            binding.updateMap.visibility = View.INVISIBLE
        }else{
            binding.mapView.visibility = View.VISIBLE
            binding.mapMessage.visibility = View.INVISIBLE
            binding.updateMap.visibility = View.VISIBLE

            binding.mapView.let {
                it.map = viewModel.createMapCountry()
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
                                val pointRequested = viewModel.getMapPointInfo(pointSelectedOnMap.get())
                                pointRequested.observe(viewLifecycleOwner, Observer { point ->
                                    point?.apply {
                                        viewModelAdapter?.pointDetails = point
                                        //println(point)
                                    }
                                })
                                showPointDetails()
                            } catch (e: Exception) {
                                Snackbar.make(activity!!.findViewById(android.R.id.content), "Point selected failed: " + e.message, Snackbar.LENGTH_LONG).show()
                            }
                        }

                        return super.onSingleTapConfirmed(motionEvent)
                    }
                }
            }
        }

        binding.updateMap.setOnClickListener {
            viewModel.refreshMap()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.lastUpdate.observe(viewLifecycleOwner, Observer {lastUpdate ->
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Last map server update: $lastUpdate", Snackbar.LENGTH_LONG).show()
        })
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

    private fun createProgressDialog(): ProgressDialog{
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Downloading the latest map for offline mode")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = false
        progressDialog.progress = 0
        return progressDialog
    }

    override fun onPause() {
        super.onPause()
        if(viewModel.tiledMap != null){
            mapView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.tiledMap != null){
            mapView.resume()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(viewModel.tiledMap != null){
            mapView.dispose()
        }
    }
}
