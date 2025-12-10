package com.cs407.myapplication.ui.apartments

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.myapplication.R
import com.cs407.myapplication.data.apartments.local.db.ApartmentDatabase
import com.cs407.myapplication.data.apartments.local.entities.ApartmentEntity
import com.cs407.myapplication.data.apartments.local.entities.FloorPlanEntity
import com.cs407.myapplication.data.apartments.repository.ApartmentRepository

@Composable
fun ApartmentDetailScreen(
    apartmentName: String,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { ApartmentDatabase.getInstance(context) }
    val repository = remember { ApartmentRepository(db.apartmentDao()) }
    val viewModel = remember { ApartmentDetailViewModel(repository) }

    val apartment by viewModel.apartment.collectAsState()
    val floorPlans by viewModel.floorPlans.collectAsState()

    // Load apartment data from DB
    LaunchedEffect(apartmentName) {
        viewModel.loadApartment(apartmentName)
    }

    if (apartment == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ApartmentDetailContent(
            apartment = apartment!!,
            floorPlans = floorPlans,
            onBackClick = onBackClick
        )
    }
}

@Composable
fun ApartmentDetailContent(
    apartment: ApartmentEntity,
    floorPlans: List<FloorPlanEntity>,
    onBackClick: () -> Unit
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
        "140 Iota Courts" -> listOf(
            R.drawable.iota,
            R.drawable.iotagallary1,
            R.drawable.iotagallary2,
            R.drawable.iotagallary3,
            R.drawable.iotagallary4
        )
        "The Langdon Apartment" -> listOf(
            R.drawable.langdon,
            R.drawable.langdongallary1,
            R.drawable.langdongallary2,
            R.drawable.langdongallary3,
            R.drawable.langdongallary4
        )
        else -> listOf(R.drawable.waterfront)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ---- Top Bar ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
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

        // ---- Image Gallery ----
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

        // ---- Apartment Info ----
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            InfoCard(
                title = "Address",
                content = if (apartment.address.isNotBlank())
                    apartment.address else "No address data available"
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "Coordinates",
                content = apartment.coordinates
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoCard(
                title = "Utilities / Features",
                content = apartment.utilities
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (floorPlans.isNotEmpty()) {
                InfoCard(
                    title = "Floor Plans",
                    content = floorPlans.joinToString("\n") {
                        "${it.bedsBath} - ${it.size} sqft - \$${it.rent}"
                    }
                )
            } else {
                InfoCard(
                    title = "Floor Plans",
                    content = "No floor plans available"
                )
            }
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
        Column(modifier = Modifier.padding(16.dp)) {
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
