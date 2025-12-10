package com.cs407.myapplication.ui.apartments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.myapplication.data.apartments.repository.ApartmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ApartmentListViewModel(private val repository: ApartmentRepository) : ViewModel() {

    // Use a simple data class for the UI
    data class ApartmentListItem(
        val id: Int,
        val name: String
    )

    private val _apartments = MutableStateFlow<List<ApartmentListItem>>(emptyList())
    val apartments: StateFlow<List<ApartmentListItem>> = _apartments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadApartments(SortOrder.NONE)
    }

    fun loadApartments(sortOrder: SortOrder) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val apartments = when (sortOrder) {
                    SortOrder.A_TO_Z -> repository.getAllApartments().sortedBy { it.name }
                    SortOrder.Z_TO_A -> repository.getAllApartments().sortedByDescending { it.name }
                    SortOrder.CHEAPEST -> repository.getApartmentsByCheapest()
                    SortOrder.MOST_EXPENSIVE -> repository.getApartmentsByMostExpensive()
                    SortOrder.SMALLEST -> repository.getApartmentsBySmallest()
                    SortOrder.LARGEST -> repository.getApartmentsByLargest()
                    SortOrder.NONE -> repository.getAllApartments()
                }

                // Convert to simple list items
                val displayApartments = apartments.map { apartment ->
                    ApartmentListItem(
                        id = apartment.id,
                        name = apartment.name
                    )
                }

                _apartments.value = displayApartments
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}