package com.cs407.myapplication.ui.apartments

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.background
import com.cs407.myapplication.ui.components.Apartment

@Composable
fun ApartmentsListScreen(onApartmentClick: (Apartment) -> Unit = {}) {
    val apartments = listOf(
        Apartment("Waterfront Apartment", R.drawable.waterfront),
        Apartment("Palisade Properties", R.drawable.palisade),
        Apartment("Aberdeen Apartments", R.drawable.aberdeen)
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

        apartments.forEach { apartment ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { onApartmentClick(apartment) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = apartment.imageRes),
                        contentDescription = "Image of ${apartment.name}",
                        modifier = Modifier
                            .size(96.dp)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = apartment.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}