package com.cs407.myapplication.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.myapplication.data.apartments.local.dao.ApartmentDao
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val dao: ApartmentDao
) : ViewModel() {

    private val _apartments = MutableStateFlow<List<ApartmentEntity>>(emptyList())
    val apartments: StateFlow<List<ApartmentEntity>> = _apartments

    init {
        viewModelScope.launch {
            _apartments.value = dao.getApartments()
        }
    }
}