package com.example.devexercise.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.geometry.Envelope
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.example.devexercise.R
import com.example.devexercise.dagger.Injectable
import com.example.devexercise.databinding.FragmentMapBinding
import com.example.devexercise.util.MapPointAdapter
import com.example.devexercise.viewmodel.CountryMapViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.mapView
import javax.inject.Inject
import kotlin.math.roundToInt

class MapFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: CountryMapViewModel by viewModels { viewModelFactory }

    private var viewModelAdapter: MapPointAdapter? = null

    private val graphicsOverlay: GraphicsOverlay by lazy { GraphicsOverlay() }
    private var downloadArea: Graphic? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)

        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        viewModelAdapter = MapPointAdapter()

        val downloadDialog = createProgressDialog()

        downloadArea = Graphic()
        downloadArea?.symbol = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 2f)
        graphicsOverlay.graphics.add(downloadArea)

        if(viewModel.tiledMap == null){
            binding.mapView.visibility = View.INVISIBLE
            binding.mapMessage.visibility = View.VISIBLE
            binding.updateMap.visibility = View.INVISIBLE
            binding.downloadMap.visibility = View.INVISIBLE
        }else{
            binding.mapView.visibility = View.VISIBLE
            binding.mapMessage.visibility = View.INVISIBLE
            binding.updateMap.visibility = View.VISIBLE
            binding.downloadMap.visibility = View.VISIBLE

            binding.mapView.let {
                it.map = viewModel.createMapCountry()
                it.graphicsOverlays.add(graphicsOverlay)
                it.onTouchListener = object : DefaultMapViewOnTouchListener(context, it){
                    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {

                        val tappedPoint = it.screenToLocation(Point(motionEvent.x.roundToInt(), motionEvent.y.roundToInt()))
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

                it.addViewpointChangedListener {
                    val minScreenPoint =  Point(200,200)
                    val maxScreenPoint = Point(mapView.width - 200, mapView.height - 200)
                    val minPoint = mapView.screenToLocation(minScreenPoint)
                    val maxPoint = mapView.screenToLocation(maxScreenPoint)
                    if(minPoint != null && maxPoint != null){
                        val envelope = Envelope(minPoint, maxPoint)
                        downloadArea?.geometry = envelope
                    }
                }
            }
        }

        binding.updateMap.setOnClickListener {
            viewModel.refreshMap()
        }

        binding.downloadMap.setOnClickListener {
            try{
                val minScale = binding.mapView.mapScale
                val maxScale = binding.mapView.map.maxScale
                viewModel.sendAreaToDownload(downloadArea!!.geometry, minScale, maxScale)
                viewModel.downloadStatus.observe(viewLifecycleOwner, Observer { downloadStatus ->
                    when(downloadStatus){
                        "PREPARED" -> Snackbar.make(requireActivity().findViewById(android.R.id.content), "Preparing download.", Snackbar.LENGTH_LONG).show()
                        "SUCCEEDED" -> Snackbar.make(requireActivity().findViewById(android.R.id.content), "Area successfully downloaded.", Snackbar.LENGTH_LONG).show()
                        "FAILED" -> Snackbar.make(requireActivity().findViewById(android.R.id.content), "The area selected exceeds the limits allowed. Please zoom in the area on the map.", Snackbar.LENGTH_LONG).show()
                    }
                })
                viewModel.downloadProgress.observe(viewLifecycleOwner, Observer {
                    downloadDialog.show()
                    downloadDialog.progress = it
                    if(downloadDialog.progress == 100) downloadDialog.dismiss()
                })
            }catch(e: Exception){
                println("Download error, please try again.")
            }
        }

        viewModel.isOnline.observe(viewLifecycleOwner, Observer { isOnline ->
            if(!isOnline){
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "You are offline now.", Snackbar.LENGTH_LONG).show()
                binding.updateMap.visibility =  View.INVISIBLE
                binding.downloadMap.visibility =  View.INVISIBLE
            }
        })

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
        progressDialog.setTitle("Downloading")
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
