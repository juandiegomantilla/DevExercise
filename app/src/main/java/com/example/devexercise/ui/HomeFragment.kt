package com.example.devexercise.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.devexercise.R
import com.example.devexercise.databinding.FragmentHomeBinding
import com.example.devexercise.databinding.ListItemCasesCountryBinding
import com.example.devexercise.repository.CountryModel
import com.example.devexercise.viewmodel.HomeViewModel
import com.example.devexercise.viewmodel.HomeViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "Unable to access the ViewModel"
        }
        ViewModelProviders.of(this, HomeViewModelFactory(activity.application))
            .get(HomeViewModel::class.java)
    }

    private var viewModelAdapter: HomeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        viewModelAdapter = HomeAdapter(CountryClick{
            viewModel.displayCountryOnMap(it)
        })

        viewModel.navigateToSelectedCountry.observe(this, Observer {
            if(null != it){
              this.findNavController().navigate(HomeFragmentDirections.showCountryInMap(it))
                viewModel.displayCountryOnMapComplete()
            }
        })

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }

        binding.root.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).setOnRefreshListener {
            swipe_refresh_layout.isRefreshing = true
            viewModel.getData()
            swipe_refresh_layout.isRefreshing = false
            println("UPDATED!!")
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

class CountryClick(val clickListener: (CountryModel) -> Unit){
    fun onClick(country:CountryModel) = clickListener(country)
}

class HomeAdapter(val onClickListener: CountryClick): RecyclerView.Adapter<HomeViewHolder>(){

    var countries: List<CountryModel> = emptyList()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val withDatabinding: ListItemCasesCountryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            HomeViewHolder.LAYOUT,
            parent,
            false)
        return HomeViewHolder(withDatabinding)
    }

    override fun getItemCount() = countries.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.viewDataBinding.also{
            it.country = countries[position]
        }
        val countryInfo = countries.get(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(countryInfo)
        }

    }
}

class HomeViewHolder(val viewDataBinding: ListItemCasesCountryBinding): RecyclerView.ViewHolder(viewDataBinding.root){
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_cases_country
    }
}

