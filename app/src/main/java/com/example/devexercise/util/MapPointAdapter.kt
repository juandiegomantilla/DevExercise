package com.example.devexercise.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.devexercise.databinding.ListDetailsPointMapBinding
import com.example.devexercise.repository.MapPointModel

class MapPointAdapter: RecyclerView.Adapter<MapPointViewHolder>(){

    var pointDetails: List<MapPointModel> = emptyList()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapPointViewHolder {
        val withDatabinding: ListDetailsPointMapBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            MapPointViewHolder.LAYOUT,
            parent,
            false)
        return MapPointViewHolder(withDatabinding)
    }

    override fun getItemCount() = pointDetails.size

    override fun onBindViewHolder(holder: MapPointViewHolder, position: Int) {
        holder.viewDataBinding.also{
            it.point = pointDetails[position]
        }
    }
}