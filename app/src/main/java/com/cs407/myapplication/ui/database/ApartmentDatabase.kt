package com.cs407.myapplication.ui.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ApartmentEntity::class, FloorPlanEntity::class],
    version = 1
)
abstract class ApartmentDatabase : RoomDatabase() {
    abstract fun apartmentDao(): ApartmentDao
}