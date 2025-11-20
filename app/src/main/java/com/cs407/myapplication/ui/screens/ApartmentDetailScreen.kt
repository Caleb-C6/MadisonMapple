package com.cs407.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.myapplication.R
import com.cs407.myapplication.ui.screens.Apartment

@Composable
fun ApartmentDetailScreen(
    apartment: Apartment,
    onBackClick: () -> Unit = {}
) {
    var currentImageIndex by remember { mutableStateOf(0) }

    val galleryImages = when (apartment.name) {
        "Waterfront Apartment" -> listOf(
            R.drawable.waterfront,
            R.drawable.waterfontgallary1,
            R.drawable.waterfontgallary2,
            R.drawable.waterfontgallary3,
            R.drawable.waterfontgallary4
        )
        "Palisade Properties" -> listOf(
            R.drawable.palisade,
            R.drawable.palisadegallary1,
            R.drawable.palisadegallary2,
            R.drawable.palisadegallary3,
            R.drawable.palisadegallary4
        )
        "Aberdeen Apartments" -> listOf(
            R.drawable.aberdeen,
            R.drawable.aberdeengallary1,
            R.drawable.aberdeengallary2,
            R.drawable.aberdeengallary3,
            R.drawable.aberdeengallary4
        )
        else -> listOf(apartment.imageRes)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = apartment.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = galleryImages[currentImageIndex]),
                contentDescription = "Gallery image ${currentImageIndex + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous image",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                        .clickable {
                            currentImageIndex =
                                (currentImageIndex - 1 + galleryImages.size) % galleryImages.size
                        }
                        .padding(8.dp),
                    tint = Color.White
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next image",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                        .clickable {
                            currentImageIndex = (currentImageIndex + 1) % galleryImages.size
                        }
                        .padding(8.dp),
                    tint = Color.White
                )
            }

            Text(
                text = "${currentImageIndex + 1}/${galleryImages.size}",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color.White,
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Location",
                content = "Address information will be added from database"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "Address",
                content = "Street address will be loaded from database"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "Features",
                content = "Apartment features and amenities will be displayed here"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "Contact",
                content = "Contact information will be available soon"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "Additional Information",
                content = "Detailed apartment description will be loaded from the database"
            )
        }
    }
}

@Composable
fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}