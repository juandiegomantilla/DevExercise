package com.example.devexercise.util

import android.view.View
import com.example.devexercise.repository.MapPointModel

class PointClick(val clickListener: (MapPointModel) -> Unit){
    fun onClick(point: MapPointModel) = clickListener(point)
}