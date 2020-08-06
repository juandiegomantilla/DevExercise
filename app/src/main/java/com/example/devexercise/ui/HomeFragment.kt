package com.example.devexercise.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.devexercise.DevExerciseApp
import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentHomeBinding
import com.example.devexercise.util.CountryClick
import com.example.devexercise.util.HomeAdapter
import com.example.devexercise.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : Fragment() {

    @Inject
    lateinit var viewModel: HomeViewModel

    private var viewModelAdapter: HomeAdapter? = null
    override fun onAttach(context: Context) {
        (context.applicationContext as DevExerciseApp).appComp().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter = HomeAdapter(CountryClick{
            viewModel.displayCountryOnMap(it)
        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        binding.root.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            viewModel.getData()
            swipe_refresh_layout.isRefreshing = false
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.dataList.observe(viewLifecycleOwner, Observer {countries ->
            countries?.apply {
                viewModelAdapter?.countries = countries
            }
        })

        viewModel.lastUpdate.observe(this, Observer {lastUpdate ->
            Snackbar.make(activity!!.findViewById(android.R.id.content), "Last server update: $lastUpdate", Snackbar.LENGTH_LONG).show()
        })

        viewModel.navigateToSelectedCountry.observe(this, Observer {
            if(null != it){
                this.findNavController().navigate(HomeFragmentDirections.showCountryInMap(it))
                viewModel.displayCountryOnMapComplete()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }
}

