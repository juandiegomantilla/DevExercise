package com.example.devexercise.network

import java.lang.Exception

sealed class Results <out T: Any>{
    data class Success<out T: Any>(val data: T): Results<T>()
    data class Error(val exception: Exception): Results<Nothing>()
    object Loading: Results<Nothing>()

    override fun toString(): String {
        return when (this){
            is Success<*> -> "Success[data=$data]"
            //is Error -> "Error[exception=$exception]"
            is Error -> "Error[message=$exception]"
            Loading -> "Loading"
        }
    }
}