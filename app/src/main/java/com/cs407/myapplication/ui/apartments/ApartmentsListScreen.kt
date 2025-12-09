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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cs407.myapplication.R
import com.cs407.myapplication.ui.components.Apartment
import androidx.compose.ui.text.style.TextAlign

// Define sort order enum
enum class SortOrder {
    A_TO_Z,
    Z_TO_A,
    NONE
}

@Composable
fun ApartmentsListScreen(
    onApartmentClick: (Apartment) -> Unit = {}
) {
    // Original apartments list
    val originalApartments = listOf(
        Apartment("Waterfront Apartment", R.drawable.waterfront),
        Apartment("Palisade Properties", R.drawable.palisade),
        Apartment("Aberdeen Apartments", R.drawable.aberdeen),
        Apartment("140 Iota Courts", R.drawable.iota),
        Apartment("The Langdon Apartment", R.drawable.langdon)
    )

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for sorting
    var sortOrder by remember { mutableStateOf(SortOrder.NONE) }

    // Filter and sort apartments
    val filteredApartments = remember(searchQuery, sortOrder) {
        var result = originalApartments.filter { apartment ->
            searchQuery.isEmpty() ||
                    apartment.name.contains(searchQuery, ignoreCase = true)
        }

        // Apply sorting
        result = when (sortOrder) {
            SortOrder.A_TO_Z -> result.sortedBy { it.name }
            SortOrder.Z_TO_A -> result.sortedByDescending { it.name }
            SortOrder.NONE -> result
        }

        result
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
                sortOrder = SortOrder.NONE // Reset sort when clearing search
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
            SortDropdownMenu(
                sortOrder = sortOrder,
                onSortOrderChange = { sortOrder = it }
            )
        }

        // Apartments List
        if (filteredApartments.isEmpty()) {
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
                items(filteredApartments) { apartment ->
                    ApartmentCard(
                        apartment = apartment,
                        onClick = { onApartmentClick(apartment) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SortDropdownMenu(
    sortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Sort Button
        FilterChip(
            selected = sortOrder != SortOrder.NONE,
            onClick = { expanded = true },
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = when (sortOrder) {
                            SortOrder.A_TO_Z -> "A → Z"
                            SortOrder.Z_TO_A -> "Z → A"
                            SortOrder.NONE -> "Sort"
                        }
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Sort options",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        )

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("A → Z (Ascending)") },
                onClick = {
                    onSortOrderChange(SortOrder.A_TO_Z)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("Z → A (Descending)") },
                onClick = {
                    onSortOrderChange(SortOrder.Z_TO_A)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null
                    )
                }
            )
            Divider()
            DropdownMenuItem(
                text = { Text("Clear Sort") },
                onClick = {
                    onSortOrderChange(SortOrder.NONE)
                    expanded = false
                },
                enabled = sortOrder != SortOrder.NONE,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

private fun getSortText(sortOrder: SortOrder): String {
    return when (sortOrder) {
        SortOrder.A_TO_Z -> "Sorted A → Z"
        SortOrder.Z_TO_A -> "Sorted Z → A"
        SortOrder.NONE -> ""
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

@Composable
fun ApartmentCard(
    apartment: Apartment,
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
            Image(
                painter = painterResource(id = apartment.imageRes),
                contentDescription = "Image of ${apartment.name}",
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
                    text = apartment.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}