package com.cs407.myapplication.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.myapplication.data.apartments.local.db.ApartmentDatabase
import com.cs407.myapplication.data.apartments.repository.ApartmentDetails
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Initialize Database
    val db = remember { ApartmentDatabase.getInstance(context) }
    val dao = db.apartmentDao()

    // State for apartments list
    var apartments by remember { mutableStateOf<List<ApartmentEntity>>(emptyList()) }

    // Fetch data
    LaunchedEffect(Unit) {
        val existing = dao.getApartments()
        if (existing.isEmpty()) {
            ApartmentDetails.preload(dao)
            apartments = dao.getApartments()
        } else {
            apartments = existing
        }
    }

    // Camera Setup (Madison, WI)
    val initLocation = LatLng(43.0731, -89.3935)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initLocation, 14.5f)
    }

    // 1. Draw the Map
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        apartments.forEach { apartment ->
            val coords = apartment.coordinates

            // Parse coordinates safely
            if (coords.isNotBlank() && coords.contains(",")) {
                val parts = coords.split(",")
                if (parts.size == 2) {
                    val lat = parts[0].trim().toDoubleOrNull()
                    val lng = parts[1].trim().toDoubleOrNull()

                    if (lat != null && lng != null) {

                        // 2. Use MarkerInfoWindow instead of just Marker
                        // This allows custom content directly above the pin.
                        MarkerInfoWindow(
                            state = MarkerState(position = LatLng(lat, lng)),
                            title = apartment.name
                        ) { marker ->
                            // --- CUSTOM INFO WINDOW CONTENT ---

                            // Calculate image resource dynamically
                            val firstWord = apartment.name.trim().split(" ").firstOrNull()?.lowercase() ?: ""
                            val imageResId = context.resources.getIdentifier(firstWord, "drawable", context.packageName)

                            // The "Box" above the marker
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .width(200.dp) // Fixed width for the popup
                                    .padding(bottom = 12.dp) // Space between box and marker
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    // Title
                                    Text(
                                        text = apartment.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    // Image
                                    if (imageResId != 0) {
                                        Image(
                                            painter = painterResource(id = imageResId),
                                            contentDescription = "Apartment Image",
                                            modifier = Modifier
                                                .height(120.dp)
                                                .width(180.dp), // Fits inside the card
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = "No Image",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
