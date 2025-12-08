package com.cs407.myapplication.data.apartments.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Schema for apartments
@Entity(tableName = "apartments")
data class ApartmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val coordinates: String,
    val utilities: String
)