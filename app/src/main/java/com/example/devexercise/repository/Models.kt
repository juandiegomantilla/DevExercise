package com.example.devexercise.repository

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CountryModel(
    val OBJECTID: Int,
    val Country_Region: String?,
    val Last_Update: String?,
    val Lat: Double?,
    val Long_: Double?,
    val Confirmed: Int?,
    val Deaths: Int?,
    val Recovered: Int?,
    val Active: Int?
) : Parcelable{}