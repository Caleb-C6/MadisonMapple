package com.cs407.myapplication.ui.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ApartmentEntity::class, FloorPlanEntity::class],
    version = 1
)
abstract class ApartmentDatabase : RoomDatabase() {
    abstract fun apartmentDao(): ApartmentDao

    companion object {
        @Volatile
        private var INSTANCE: ApartmentDatabase? = null

        fun getInstance(context: Context): ApartmentDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ApartmentDatabase::class.java,
                    "apartments.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}