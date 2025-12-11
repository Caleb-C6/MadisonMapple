package com.cs407.myapplication.ui.apartments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import com.cs407.myapplication.data.apartments.local.entities.FloorPlanEntity
import com.cs407.myapplication.data.apartments.repository.ApartmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApartmentDetailViewModel(private val repository: ApartmentRepository) : ViewModel() {

    private val _apartment = MutableStateFlow<ApartmentEntity?>(null)
    val apartment: StateFlow<ApartmentEntity?> = _apartment

    private val _floorPlans = MutableStateFlow<List<FloorPlanEntity>>(emptyList())
    val floorPlans: StateFlow<List<FloorPlanEntity>> = _floorPlans

    fun loadApartment(displayName: String) {
        viewModelScope.launch {
            val databaseName = ApartmentNameMapper.getDatabaseName(displayName)
            val apt = repository.getApartmentByName(databaseName)

            _apartment.value = apt
            _floorPlans.value = if (apt != null) {
                repository.getFloorPlans(apt.id)
            } else {
                emptyList()
            }
        }
    }
}
