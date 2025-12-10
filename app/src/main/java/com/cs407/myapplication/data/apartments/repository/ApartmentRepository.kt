package com.cs407.myapplication.data.apartments.repository

import com.cs407.myapplication.data.apartments.local.dao.ApartmentDao
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import com.cs407.myapplication.data.apartments.local.entities.FloorPlanEntity

class ApartmentRepository(private val dao: ApartmentDao) {

    suspend fun getApartmentByName(name: String): ApartmentEntity? =
        dao.getApartmentByName(name)

    suspend fun getFloorPlans(apartmentId: Int): List<FloorPlanEntity> =
        dao.getFloorPlans(apartmentId)
}
