package com.cs407.myapplication.ui.roommates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.myapplication.ui.profile.UserProfile

/* ---------------------------------------------------------
   MAIN ROOMMATE BROWSING SCREEN
--------------------------------------------------------- */

@Composable
fun RoommateBrowseScreen(
    viewModel: RoommateViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Loading spinner
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /* -----------------------------
           HEADER
        ------------------------------ */
        Text(
            text = "Roommate Matching",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Browse other profiles and save the ones you like!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        /* -----------------------------
           FILTERS (ALL vs FAVORITES)
        ------------------------------ */
        FilterRow(
            showFavoritesOnly = state.showFavoritesOnly,
            onToggleFavoritesOnly = { viewModel.toggleShowFavoritesOnly() }
        )

        Spacer(Modifier.height(12.dp))

        /* -----------------------------
           ERROR MESSAGE
        ------------------------------ */
        state.errorMsg?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }

        /* -----------------------------
           FILTER VISIBLE PROFILES
        ------------------------------ */
        val visibleProfiles = remember(state.allProfiles, state.favoriteIds, state.showFavoritesOnly) {
            if (state.showFavoritesOnly) {
                state.allProfiles.filter { state.favoriteIds.contains(it.uid) }
            } else {
                state.allProfiles
            }
        }

        /* -----------------------------
           DISPLAY LIST OR EMPTY STATE
        ------------------------------ */
        if (visibleProfiles.isEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = if (state.showFavoritesOnly)
                    "No favorites yet. Heart some profiles to see them here."
                else
                    "No other users found.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleProfiles, key = { it.uid }) { profile ->
                    val isFavorite = state.favoriteIds.contains(profile.uid)
                    RoommateProfileCard(
                        profile = profile,
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavoriteFor(profile) }
                    )
                }
            }
        }
    }
}

/* ---------------------------------------------------------
   FILTER ROW ("All" vs "Favorites")
--------------------------------------------------------- */

@Composable
private fun FilterRow(
    showFavoritesOnly: Boolean,
    onToggleFavoritesOnly: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,   // Center group
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp), // spacing between buttons
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChipButton(
                text = "All profiles",
                selected = !showFavoritesOnly,
                onClick = {
                    if (showFavoritesOnly) onToggleFavoritesOnly()
                }
            )

            FilterChipButton(
                text = "Favorites only",
                selected = showFavoritesOnly,
                onClick = {
                    if (!showFavoritesOnly) onToggleFavoritesOnly()
                }
            )
        }
    }
}

@Composable
private fun FilterChipButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        colors = if (selected) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            ButtonDefaults.filledTonalButtonColors()
        }
    ) {
        Text(text)
    }
}

/* ---------------------------------------------------------
   PROFILE CARD
--------------------------------------------------------- */

@Composable
fun RoommateProfileCard(
    profile: UserProfile,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            /* -------------------------
               TOP ROW: NAME + HEART
            -------------------------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = profile.hometown,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleFavorite) {
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Unfavorite",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            /* -------------------------
               INTERESTS
            -------------------------- */
            Text(
                text = "Interests: ${profile.interests}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            /* -------------------------
               ROUTINE
            -------------------------- */
            Text(
                text = "Daily routine: wakes ${profile.wakeUpTime?.label}, sleeps ${profile.sleepTime?.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            /* -------------------------
               STUDY + SOCIAL
            -------------------------- */
            Text(
                text = "Study: ${profile.studyHabits?.label} • Social: ${profile.partyingFrequency?.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            /* -------------------------
               SLIDER SUMMARIES
            -------------------------- */
            Text(
                text = "${describeScale("Cleanliness", profile.cleanliness)} • " +
                        "${describeScale("Noise tolerance", profile.noiseTolerance)} • " +
                        "${describeScale("Introvert/Extrovert", profile.introvertExtrovert)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            /* -------------------------
               LIFESTYLE FLAGS
            -------------------------- */
            val lifestyleBits = listOf(
                if (profile.smoker) "Smoker" else "Non-smoker",
                if (profile.drinker) "Drinks" else "Doesn't drink",
                if (profile.pets) "Pets" else "No pets",
                if (profile.guestsAllowed) "OK with guests" else "",
                if (profile.overnightGuests) "Overnight guests OK" else ""
            ).filter { it.isNotBlank() }

            Text(
                text = lifestyleBits.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            /* -------------------------
               HOUSING
            -------------------------- */
            Text(
                text = "Housing: ${profile.roomTypePreference?.label} • ${profile.budgetRange}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            /* -------------------------
               SOCIAL LINKS
            -------------------------- */
            Spacer(Modifier.height(6.dp))

            Text(
                text = "Contact: ${profile.socialLinks}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/* ---------------------------------------------------------
   HELPER
--------------------------------------------------------- */

private fun describeScale(label: String, value: Int): String {
    val desc = when (value) {
        1, 2 -> "Low"
        3 -> "Medium"
        4, 5 -> "High"
        else -> "Medium"
    }
    return "$label: $desc"
}
