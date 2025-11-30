package com.cs407.myapplication.ui.database

import androidx.room.Entity
import androidx.room.PrimaryKey

//Schema for floorplans

@Entity(tableName = "floorplans")
data class FloorPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val apartmentId: Int,
    val size: String,
    val bedsBath: String,
    val rent: String
)