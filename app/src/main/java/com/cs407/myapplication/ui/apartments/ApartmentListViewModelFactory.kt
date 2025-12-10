package com.cs407.myapplication.ui.apartments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cs407.myapplication.data.apartments.repository.ApartmentRepository

class ApartmentListViewModelFactory(
    private val repository: ApartmentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApartmentListViewModel::class.java)) {
            return ApartmentListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}