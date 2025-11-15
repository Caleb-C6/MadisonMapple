package com.cs407.myapplication.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState



@Composable
fun MapScreen() {
// Defines a state object that controls the camera's position on the map
    val initLocation = LatLng(43.0731, -89.4012) // Hardcoded coordinates (Singapore)
    val cameraPositionState = rememberCameraPositionState {
// Sets the initial position and zoom level of the map
        position = CameraPosition.fromLatLngZoom(initLocation, 14f)
    }
// The GoogleMap composable displays the map UI inside your Compose layout
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Waterfront Apartment
        Marker(
            state = MarkerState(position = LatLng(43.07761, -89.39298)),
            title = "Waterfront Apartment",
        )

        // Palisade Properties
        Marker(
            state = MarkerState(position = LatLng(43.07174, -89.39501)),
            title = "Palisade Properties",
        )

        // Aberdeen Apartments
        Marker(
            state = MarkerState(position = LatLng(43.07321, -89.39356)),
            title = "Aberdeen Apartments",
        )
    }
}