package com.cs407.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cs407.myapplication.data.apartments.local.db.ApartmentDatabase
import com.cs407.myapplication.data.apartments.repository.ApartmentDetails
import com.cs407.myapplication.ui.components.SlidingMenu
import com.cs407.myapplication.ui.theme.MadisonMappleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database with sample data
        CoroutineScope(Dispatchers.IO).launch {
            val database = ApartmentDatabase.getInstance(this@MainActivity)
            ApartmentDetails.preload(database.apartmentDao())
            println("DEBUG: Database initialized and preloaded")

            // Optional: Test if data was loaded
            val dao = database.apartmentDao()
            val count = dao.getApartments().size
            println("DEBUG: Loaded $count apartments into database")
        }

        setContent {
            MadisonMappleTheme {
                SlidingMenu()
            }
        }
    }
}