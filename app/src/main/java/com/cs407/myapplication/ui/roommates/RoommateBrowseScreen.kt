package com.cs407.myapplication.ui.roommates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.myapplication.domain.roommates.RoommateViewModel
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoommateProfileCard(
    profile: UserProfile,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .widthIn(max = 500.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            Modifier
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            /* -------------------------------------------------------
               NAME + HEART
            -------------------------------------------------------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = profile.hometown,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector =
                            if (isFavorite) Icons.Filled.Favorite
                            else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            /* -------------------------------------------------------
               INTERESTS
            -------------------------------------------------------- */
            SectionTitle("Interests", Icons.Default.EmojiEmotions)
            Text(
                text = profile.interests,
                style = MaterialTheme.typography.bodyMedium
            )

            /* -------------------------------------------------------
               DAILY ROUTINE
            -------------------------------------------------------- */
            SectionTitle("Daily routine", Icons.Default.Schedule)
            ChipFlow(
                listOf(
                    "Wake: ${profile.wakeUpTime?.label}",
                    "Sleep: ${profile.sleepTime?.label}"
                )
            )

            /* -------------------------------------------------------
               PERSONALITY
            -------------------------------------------------------- */
            SectionTitle("Personality", Icons.Default.Person)
            ChipFlow(
                listOf(
                    "Cleanliness: ${describeScaleSimple(profile.cleanliness)}",
                    "Noise: ${describeScaleSimple(profile.noiseTolerance)}",
                    "Social: ${describeScaleSimple(profile.introvertExtrovert)}",
                    "Study: ${profile.studyHabits?.label ?: ""}",
                    "Parties: ${profile.partyingFrequency?.label ?: ""}"
                )
            )

            /* -------------------------------------------------------
               LIFESTYLE
            -------------------------------------------------------- */
            SectionTitle("Lifestyle", Icons.Default.LocalCafe)
            ChipFlow(
                listOfNotNull(
                    if (profile.smoker) "Smoker" else "Non-smoker",
                    if (profile.drinker) "Drinks" else "No alcohol",
                    if (profile.pets) "Pets" else "No pets",
                    if (profile.guestsAllowed) "Guests OK" else null,
                    if (profile.overnightGuests) "Overnights OK" else null
                )
            )

            /* -------------------------------------------------------
               HOUSING
            -------------------------------------------------------- */
            SectionTitle("Housing", Icons.Default.Home)
            ChipFlow(
                listOf(
                    profile.roomTypePreference?.label ?: "",
                    "Budget: ${profile.budgetRange}"
                )
            )

            /* -------------------------------------------------------
               CONTACT
            -------------------------------------------------------- */
            SectionTitle("Contact", Icons.Default.AccountCircle)
            Text(
                text = profile.socialLinks,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/* ---------------------------------------------------------
   HELPERS
--------------------------------------------------------- */

@Composable
private fun SectionTitle(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(labels: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        labels.forEach { label ->
            AssistChip(
                onClick = {},
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

private fun describeScaleSimple(value: Int) =
    when (value) {
        in 1..2 -> "Low"
        3 -> "Medium"
        in 4..5 -> "High"
        else -> "Medium"
    }
