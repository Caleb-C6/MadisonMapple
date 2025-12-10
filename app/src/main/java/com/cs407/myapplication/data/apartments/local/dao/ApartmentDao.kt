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

    @Query("SELECT * FROM apartments WHERE LOWER(name) LIKE '%' || LOWER(:name) || '%' LIMIT 1")
    suspend fun getApartmentByName(name: String): ApartmentEntity?

    // NEW QUERIES FOR SORTING

    // Get minimum rent for each apartment
    @Query("""
        SELECT a.*, MIN(
            CAST(SUBSTR(f.rent, 1, INSTR(f.rent, '-') - 1) AS INTEGER)
        ) as minRent
        FROM apartments a
        LEFT JOIN floorplans f ON a.id = f.apartmentId
        GROUP BY a.id
        ORDER BY minRent ASC
    """)
    suspend fun getApartmentsByCheapest(): List<ApartmentEntity>

    // Get maximum rent for each apartment
    @Query("""
        SELECT a.*, MAX(
            CAST(SUBSTR(f.rent, 1, INSTR(f.rent, '-') - 1) AS INTEGER)
        ) as maxRent
        FROM apartments a
        LEFT JOIN floorplans f ON a.id = f.apartmentId
        GROUP BY a.id
        ORDER BY maxRent DESC
    """)
    suspend fun getApartmentsByMostExpensive(): List<ApartmentEntity>

    // Get minimum size for each apartment
    @Query("""
        SELECT a.*, MIN(
            CAST(SUBSTR(f.size, 1, INSTR(f.size, '-') - 1) AS INTEGER)
        ) as minSize
        FROM apartments a
        LEFT JOIN floorplans f ON a.id = f.apartmentId
        GROUP BY a.id
        ORDER BY minSize ASC
    """)
    suspend fun getApartmentsBySmallest(): List<ApartmentEntity>

    // Get maximum size for each apartment
    @Query("""
        SELECT a.*, MAX(
            CAST(SUBSTR(f.size, 1, INSTR(f.size, '-') - 1) AS INTEGER)
        ) as maxSize
        FROM apartments a
        LEFT JOIN floorplans f ON a.id = f.apartmentId
        GROUP BY a.id
        ORDER BY maxSize DESC
    """)
    suspend fun getApartmentsByLargest(): List<ApartmentEntity>
}