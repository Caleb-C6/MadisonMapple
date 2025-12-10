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

    fun loadApartment(displayName: String) {  // Changed parameter name for clarity
        viewModelScope.launch {
            // Convert display name to database name using the mapper
            val databaseName = ApartmentNameMapper.getDatabaseName(displayName)

            // Add logging for debugging
            println("DEBUG: Converting display name '$displayName' to database name '$databaseName'")

            val apt = repository.getApartmentByName(databaseName)

            // More logging
            println("DEBUG: Found apartment: ${apt?.name ?: "NOT FOUND"}")

            _apartment.value = apt
            if (apt != null) {
                _floorPlans.value = repository.getFloorPlans(apt.id)
                println("DEBUG: Loaded ${_floorPlans.value.size} floor plans")
            } else {
                _floorPlans.value = emptyList()
            }
        }
    }
}