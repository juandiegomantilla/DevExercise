package com.example.devexercise.util

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.devexercise.R
import com.example.devexercise.databinding.ListItemCasesCountryBinding

class HomeViewHolder(val viewDataBinding: ListItemCasesCountryBinding): RecyclerView.ViewHolder(viewDataBinding.root){
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.list_item_cases_country
    }
}