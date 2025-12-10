package com.cs407.myapplication.data.apartments.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import com.cs407.myapplication.data.apartments.local.entities.FloorPlanEntity
import com.cs407.myapplication.data.apartments.local.dao.ApartmentDao

@Database(
    entities = [ApartmentEntity::class, FloorPlanEntity::class],
    version = 2,  // Change from 1 to 2
    exportSchema = false
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
                )
                    .fallbackToDestructiveMigration()  // Add this line to handle schema changes
                    .build().also { INSTANCE = it }
            }
        }
    }
}