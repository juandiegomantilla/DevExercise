package com.example.devexercise.util

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.devexercise.R
import com.example.devexercise.databinding.ListDetailsPointMapBinding

class MapPointViewHolder(val viewDataBinding: ListDetailsPointMapBinding): RecyclerView.ViewHolder(viewDataBinding.root){
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_details_point_map
    }
}