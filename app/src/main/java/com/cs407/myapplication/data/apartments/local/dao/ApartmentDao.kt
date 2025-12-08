package com.cs407.myapplication.data.apartments.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import com.cs407.myapplication.data.apartments.local.entities.FloorPlanEntity

@Dao
interface ApartmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApartment(apartment: ApartmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFloorPlan(floorplan: FloorPlanEntity)

    @Transaction
    @Query("SELECT * FROM apartments")
    suspend fun getApartments(): List<ApartmentEntity>

    @Query("SELECT * FROM floorplans WHERE apartmentId = :apartmentId")
    suspend fun getFloorPlans(apartmentId: Int): List<FloorPlanEntity>
}