package com.cs407.myapplication.ui.database

import androidx.room.*

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