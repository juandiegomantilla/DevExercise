package com.example.devexercise.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.devexercise.databinding.ListItemCasesCountryBinding
import com.example.devexercise.repository.CountryModel

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