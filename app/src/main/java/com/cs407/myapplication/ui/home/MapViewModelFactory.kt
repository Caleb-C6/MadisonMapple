package com.cs407.myapplication.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cs407.myapplication.data.apartments.local.dao.ApartmentDao

class MapViewModelFactory(
    private val dao: ApartmentDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}