package com.cs407.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.cs407.myapplication.R

@Composable
fun ApartmentsListScreen() {
    // Dummy list of apartments
    val apartments = listOf(
        "Waterfront Apartment",
        "Palisade Properties",
        "Aberdeen Apartments"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Apartments List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        apartments.forEach { apartmentName ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Height for image + text
                    .clickable { /* TODO: Navigate to details */ },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder image
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(Color.Gray)
                    ) {
                        // Later replace with Image composable

                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Apartment name
                    Text(
                        text = apartmentName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
