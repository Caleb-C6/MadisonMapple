package com.cs407.myapplication.ui.apartments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cs407.myapplication.R
import com.cs407.myapplication.ui.components.Apartment
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.myapplication.data.apartments.local.db.ApartmentDatabase
import com.cs407.myapplication.data.apartments.repository.ApartmentRepository

// SortOrder enum
enum class SortOrder {
    A_TO_Z,
    Z_TO_A,
    CHEAPEST,
    MOST_EXPENSIVE,
    SMALLEST,
    LARGEST,
    NONE
}

@Composable
fun ApartmentsListScreen(
    onApartmentClick: (Apartment) -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { ApartmentDatabase.getInstance(context) }
    val repository = remember { ApartmentRepository(database.apartmentDao()) }
    val viewModel: ApartmentListViewModel = viewModel(
        factory = ApartmentListViewModelFactory(repository)
    )

    val apartments by viewModel.apartments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for sorting
    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }

    // Load apartments when sort order changes
    LaunchedEffect(sortOrder) {
        viewModel.loadApartments(sortOrder)
    }

    // Filter apartments based on search query
    val filteredApartments = remember(apartments, searchQuery) {
        if (searchQuery.isEmpty()) {
            apartments
        } else {
            apartments.filter { apartmentListItem ->
                val displayName = ApartmentNameMapper.getDisplayName(apartmentListItem.name)
                displayName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header with title
        Text(
            text = "Apartments List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Search Bar
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onClearSearch = {
                searchQuery = ""
                sortOrder = SortOrder.NONE
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Filter and Sort Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Showing text
            Text(
                text = when {
                    sortOrder != SortOrder.NONE -> getSortText(sortOrder)
                    else -> "Showing All Apartments"
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Right side: Sort button only
            SimpleSortDropdownMenu(
                sortOrder = sortOrder,
                onSortOrderChange = { sortOrder = it }
            )
        }

        // Apartments List
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (filteredApartments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No results",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty())
                            "No apartments found for \"$searchQuery\""
                        else
                            "No apartments found",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    // Show reset button if search or sort active
                    if (searchQuery.isNotEmpty() || sortOrder != SortOrder.NONE) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                searchQuery = ""
                                sortOrder = SortOrder.NONE
                            }
                        ) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApartments) { apartmentListItem ->
                    SimpleApartmentCard(
                        apartmentListItem = apartmentListItem,
                        onClick = {
                            val displayName = ApartmentNameMapper.getDisplayName(apartmentListItem.name)
                            val imageRes = getImageResourceForDisplayName(displayName)
                            onApartmentClick(Apartment(displayName, imageRes))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleSortDropdownMenu(
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Simple Sort Button
        FilterChip(
            selected = sortOrder != SortOrder.NONE,
            onClick = { expanded = true },
            label = {
                Text(
                    text = when (sortOrder) {
                        SortOrder.A_TO_Z -> "A to Z"
                        SortOrder.Z_TO_A -> "Z to A"
                        SortOrder.CHEAPEST -> "Cheapest"
                        SortOrder.MOST_EXPENSIVE -> "Most Expensive"
                        SortOrder.SMALLEST -> "Smallest"
                        SortOrder.LARGEST -> "Largest"
                        SortOrder.NONE -> "Sort"
                    }
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Sort options"
                )
            }
        )

        // Simple Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Alphabetical Sorting
            DropdownMenuItem(
                text = { Text("A to Z") },
                onClick = {
                    onSortOrderChange(SortOrder.A_TO_Z)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Z to A") },
                onClick = {
                    onSortOrderChange(SortOrder.Z_TO_A)
                    expanded = false
                }
            )

            Divider()

            // Price Sorting
            DropdownMenuItem(
                text = { Text("Cheapest") },
                onClick = {
                    onSortOrderChange(SortOrder.CHEAPEST)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Most Expensive") },
                onClick = {
                    onSortOrderChange(SortOrder.MOST_EXPENSIVE)
                    expanded = false
                }
            )

            Divider()

            // Size Sorting
            DropdownMenuItem(
                text = { Text("Smallest") },
                onClick = {
                    onSortOrderChange(SortOrder.SMALLEST)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Largest") },
                onClick = {
                    onSortOrderChange(SortOrder.LARGEST)
                    expanded = false
                }
            )

            Divider()

            // Clear Sort
            DropdownMenuItem(
                text = { Text("Clear Sort") },
                onClick = {
                    onSortOrderChange(SortOrder.NONE)
                    expanded = false
                },
                enabled = sortOrder != SortOrder.NONE
            )
        }
    }
}

private fun getSortText(sortOrder: SortOrder): String {
    return when (sortOrder) {
        SortOrder.A_TO_Z -> "Sorted A to Z"
        SortOrder.Z_TO_A -> "Sorted Z to A"
        SortOrder.CHEAPEST -> "Sorted by Price (Low to High)"
        SortOrder.MOST_EXPENSIVE -> "Sorted by Price (High to Low)"
        SortOrder.SMALLEST -> "Sorted by Size (Small to Large)"
        SortOrder.LARGEST -> "Sorted by Size (Large to Small)"
        SortOrder.NONE -> ""
    }
}

@Composable
fun SimpleApartmentCard(
    apartmentListItem: ApartmentListViewModel.ApartmentListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Get image for this apartment
            val displayName = ApartmentNameMapper.getDisplayName(apartmentListItem.name)
            val imageRes = getImageResourceForDisplayName(displayName)

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Image of $displayName",
                modifier = Modifier
                    .size(110.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

// Helper function to get image resource
private fun getImageResourceForDisplayName(displayName: String): Int {
    return when (displayName) {
        "Waterfront Apartment" -> R.drawable.waterfront
        "Palisade Properties" -> R.drawable.palisade
        "Aberdeen Apartments" -> R.drawable.aberdeen
        "140 Iota Courts" -> R.drawable.iota
        "The Langdon Apartment" -> R.drawable.langdon
        else -> R.drawable.waterfront
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        placeholder = {
            Text("Search apartments...")
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.large,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { /* Handle search action if needed */ }
        )
    )
}

