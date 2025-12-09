package com.cs407.myapplication.ui.apartments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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

@Composable
fun ApartmentsListScreen(
    onApartmentClick: (Apartment) -> Unit = {}
) {
    // Original apartments list (only the 5 from your original code)
    val originalApartments = listOf(
        Apartment("Waterfront Apartment", R.drawable.waterfront),
        Apartment("Palisade Properties", R.drawable.palisade),
        Apartment("Aberdeen Apartments", R.drawable.aberdeen),
        Apartment("140 Iota Courts", R.drawable.iota),
        Apartment("The Langdon Apartment", R.drawable.langdon)
    )

    // State for search query
    var searchQuery by remember { mutableStateOf("") }

    // State for showing only favorites
    var showOnlyFavorites by remember { mutableStateOf(false) }

    // State for tracking favorites (persistent across recompositions)
    val favoriteIds = remember { mutableStateMapOf<Int, Boolean>() }

    // Initialize favoriteIds if empty
    LaunchedEffect(Unit) {
        if (favoriteIds.isEmpty()) {
            originalApartments.forEach { apartment ->
                favoriteIds[apartment.hashCode()] = false
            }
        }
    }

    // Filter apartments based on search query and favorite filter
    val filteredApartments = remember(searchQuery, showOnlyFavorites, favoriteIds) {
        originalApartments.filter { apartment ->
            val matchesSearch = searchQuery.isEmpty() ||
                    apartment.name.contains(searchQuery, ignoreCase = true)
            val matchesFavorite = !showOnlyFavorites ||
                    (favoriteIds[apartment.hashCode()] ?: false)
            matchesSearch && matchesFavorite
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
            onClearSearch = { searchQuery = "" },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Favorites Filter Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showOnlyFavorites) "Showing Favorites Only" else "Showing All Apartments",
                style = MaterialTheme.typography.bodyMedium
            )

            FilterChip(
                selected = showOnlyFavorites,
                onClick = { showOnlyFavorites = !showOnlyFavorites },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorites",
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Favorites Only")
                    }
                },
                leadingIcon = if (showOnlyFavorites) {
                    {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
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
                Text(
                    text = if (searchQuery.isNotEmpty())
                        "No apartments found for \"$searchQuery\""
                    else
                        "No apartments found",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApartments) { apartment ->
                    ApartmentCard(
                        apartment = apartment,
                        isFavorite = favoriteIds[apartment.hashCode()] ?: false,
                        onFavoriteClick = {
                            val currentState = favoriteIds[apartment.hashCode()] ?: false
                            favoriteIds[apartment.hashCode()] = !currentState
                        },
                        onClick = { onApartmentClick(apartment) }
                    )
                }
            }
        }
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
                        painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
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
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
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
                    // You can add more apartment details here if needed
                }
            }

            // Favorite Icon Button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite)
                        "Remove from favorites"
                    else
                        "Add to favorites",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}