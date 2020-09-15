package com.example.devexercise.ui

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.devexercise.R
import com.example.devexercise.dagger.Injectable
import com.example.devexercise.databinding.FragmentHomeBinding
import com.example.devexercise.util.CountryClick
import com.example.devexercise.util.HomeAdapter
import com.example.devexercise.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private var viewModelAdapter: HomeAdapter? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModel.presentData()

        viewModelAdapter = HomeAdapter(CountryClick{
            viewModel.displayCountryOnMap(it)
        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            viewModel.getData()
            swipe_refresh_layout.isRefreshing = false
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dataList.observe(viewLifecycleOwner, Observer {countries ->
            countries?.apply {
                viewModelAdapter?.countries = countries
            }
        })

        viewModel.lastUpdate.observe(viewLifecycleOwner, Observer {lastUpdate ->
            Snackbar.make(requireActivity().findViewById(android.R.id.content), lastUpdate, Snackbar.LENGTH_LONG).show()
        })

        viewModel.navigateToSelectedCountry.observe(viewLifecycleOwner, Observer {
            if(null != it){
                this.findNavController().navigate(HomeFragmentDirections.showCountryInMap(it))
                viewModel.displayCountryOnMapComplete()
            }
        })

        viewModel.isOnline.observe(viewLifecycleOwner, Observer { isOnline ->
            if(!isOnline){
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "You are offline now.", Snackbar.LENGTH_LONG).show()
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
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}

