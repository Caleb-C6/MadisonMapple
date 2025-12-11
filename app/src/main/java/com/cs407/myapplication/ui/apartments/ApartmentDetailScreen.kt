package com.cs407.myapplication.ui.apartments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

    LaunchedEffect(apartmentName) {
        viewModel.loadApartment(apartmentName)
    }

    if (apartment == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Loading apartment details...")
            }
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
        "Waterfront" -> listOf(
            R.drawable.waterfront,
            R.drawable.waterfontgallary1,
            R.drawable.waterfontgallary2,
            R.drawable.waterfontgallary5,
            R.drawable.waterfontgallary4
        )
        "Palisade" -> listOf(
            R.drawable.palisade,
            R.drawable.palisadegallary5,
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
        "Iota Courts" -> listOf(
            R.drawable.iota,
            R.drawable.iotagallary1,
            R.drawable.iotagallary2,
            R.drawable.iotagallary3,
            R.drawable.iotagallary4
        )
        "Langdon" -> listOf(
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
        // Top Bar
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

        // Image Gallery
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = galleryImages[currentImageIndex]),
                contentDescription = "Gallery image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Image counter
            Text(
                text = "${currentImageIndex + 1}/${galleryImages.size}",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color.White,
                fontSize = 14.sp
            )

            // Navigation arrows
            if (galleryImages.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            currentImageIndex = (currentImageIndex - 1 + galleryImages.size) % galleryImages.size
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            currentImageIndex = (currentImageIndex + 1) % galleryImages.size
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Apartment Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Apartment name header
            Text(
                text = apartment.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Address section
            SectionHeader("📍 Address")
            InfoRow(
                if (apartment.address.isNotBlank()) apartment.address else "No address available"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Coordinates section
            SectionHeader("🗺️ Coordinates")
            InfoRow(formatCoordinates(apartment.coordinates))

            Spacer(modifier = Modifier.height(12.dp))

            // Utilities section
            SectionHeader("🔌 Included Utilities")
            InfoRow(apartment.utilities)

            Spacer(modifier = Modifier.height(12.dp))

            // Amenities section
            SectionHeader("🏊 Amenities")
            if (apartment.amenities.isNotBlank()) {
                // Split amenities by comma and display as bullet points
                val amenitiesList = apartment.amenities.split(",").map { it.trim() }
                Column {
                    amenitiesList.forEach { amenity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "•",
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = amenity,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            } else {
                InfoRow("No amenities listed")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact section
            SectionHeader("📞 Contact Information")
            Column {
                // Phone
                if (apartment.contactPhone.isNotBlank() && apartment.contactPhone != "No phone") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = apartment.contactPhone,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // Email
                if (apartment.contactEmail.isNotBlank() && apartment.contactEmail != "No email") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = apartment.contactEmail,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // If no contact info
                if ((apartment.contactPhone.isBlank() || apartment.contactPhone == "No phone") &&
                    (apartment.contactEmail.isBlank() || apartment.contactEmail == "No email")) {
                    InfoRow("No contact information available")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Floor Plans section
            SectionHeader("🏢 Floor Plans")

            if (floorPlans.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    floorPlans.forEach { floorPlan ->
                        FloorPlanCardSimple(floorPlan = floorPlan)
                    }
                }
            } else {
                InfoRow("No floor plans available")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun InfoRow(content: String) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun FloorPlanCardSimple(floorPlan: FloorPlanEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with beds and bath
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Bed,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = floorPlan.bedsBath,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Size
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SquareFoot,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Size: ${formatSize(floorPlan.size)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rent
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rent: ${formatRent(floorPlan.rent)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// Helper functions for formatting
private fun formatCoordinates(coordinates: String): String {
    return try {
        val cleaned = coordinates.replace("\\s+".toRegex(), "")
        if (cleaned.contains(',')) {
            val parts = cleaned.split(",")
            if (parts.size >= 2) {
                val lat = String.format("%.5f", parts[0].toDouble())
                val lng = String.format("%.5f", parts[1].toDouble())
                "$lat, $lng"
            } else {
                coordinates
            }
        } else {
            coordinates
        }
    } catch (e: Exception) {
        coordinates
    }
}

private fun formatSize(size: String): String {
    return try {
        val parts = size.split("-").map { it.trim() }.map { it.toIntOrNull() }
        if (parts.size == 2 && parts[0] != null && parts[1] != null) {
            val formattedMin = String.format("%,d", parts[0]!!)
            val formattedMax = String.format("%,d", parts[1]!!)
            "$formattedMin - $formattedMax sq ft"
        } else {
            "$size sq ft"
        }
    } catch (e: Exception) {
        "$size sq ft"
    }
}

private fun formatRent(rent: String): String {
    return try {
        val parts = rent.split("-").map { it.trim() }.map { it.toIntOrNull() }
        if (parts.size == 2 && parts[0] != null && parts[1] != null) {
            val formattedMin = String.format("$%,d", parts[0]!!)
            val formattedMax = String.format("$%,d", parts[1]!!)
            "$formattedMin - $formattedMax"
        } else {
            "$$rent"
        }
    } catch (e: Exception) {
        "$$rent"
    }
}