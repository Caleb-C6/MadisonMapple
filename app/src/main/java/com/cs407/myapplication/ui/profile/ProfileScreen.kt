package com.cs407.myapplication.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.myapplication.domain.profile.ProfileViewModel
import com.cs407.myapplication.ui.auth.AuthManager

/* ---------------------------------------------------------
   SECTION HEADER
--------------------------------------------------------- */
@Composable
fun SectionHeader(
    title: String,
    onClear: (() -> Unit)? = null
) {
    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        if (onClear != null) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear section",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    Spacer(Modifier.height(6.dp))
    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    Spacer(Modifier.height(12.dp))
}

/* ---------------------------------------------------------
   MAIN PROFILE SCREEN
--------------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val currentUser = AuthManager.auth.currentUser
    val scrollState = rememberScrollState()

    var showDeleteWarningDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Track previous saving state so we only toast when a save finishes successfully
    var lastIsSaving by remember { mutableStateOf(false) }

    /* ---------------------------------------------------------
       Toast on successful save (no validation errors / failures)
    --------------------------------------------------------- */
    LaunchedEffect(state.isSaving, state.errorMsg) {
        // If we WERE saving and now we're NOT, and there's no error -> success
        if (lastIsSaving && !state.isSaving && state.errorMsg == null) {
            Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
        }
        lastIsSaving = state.isSaving
    }

    /* ---------------------------------------------------------
       React to completed deletion
    --------------------------------------------------------- */
    LaunchedEffect(state.deleteCompleted) {
        if (state.deleteCompleted) {
            Toast.makeText(context, "Account deleted.", Toast.LENGTH_SHORT).show()
            onAccountDeleted()
            viewModel.consumeDeleteCompleted()
        }
    }

    /* ---------------------------------------------------------
       LOADING SPINNER
    --------------------------------------------------------- */
    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    /* ---------------------------------------------------------
       MAIN UI
    --------------------------------------------------------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Your Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = currentUser?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        /* -------------------------
           BASIC INFO
        -------------------------- */
        SectionHeader("Basic Information", onClear = viewModel::clearBasicInfo)

        OutlinedTextField(
            value = state.displayName,
            onValueChange = viewModel::updateDisplayName,
            label = { Text("Name *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.hometown,
            onValueChange = viewModel::updateHometown,
            label = { Text("Hometown *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.interests,
            onValueChange = viewModel::updateInterests,
            label = { Text("Interests & hobbies *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        /* -------------------------
           DAILY ROUTINE
        -------------------------- */
        SectionHeader("Daily Routine", onClear = viewModel::clearDailyRoutine)

        EnumDropdown(
            label = "Wake-up time *",
            selected = state.wakeUpTime,
            options = WakeUpTime.entries,
            display = { it.label },
            onSelect = viewModel::updateWakeUp
        )

        Spacer(Modifier.height(12.dp))

        EnumDropdown(
            label = "Sleep time *",
            selected = state.sleepTime,
            options = SleepTime.entries.toList(),
            display = { it.label },
            onSelect = viewModel::updateSleep
        )

        /* -------------------------
           HABITS
        -------------------------- */
        SectionHeader("Habits & Environment", onClear = viewModel::clearHabits)

        SliderField("Cleanliness", state.cleanliness, viewModel::updateCleanliness)
        SliderField("Noise tolerance", state.noiseTolerance, viewModel::updateNoise)
        SliderField("Introvert <--> Extrovert", state.introvertExtrovert, viewModel::updateIntrovert)

        Spacer(Modifier.height(12.dp))

        EnumDropdown(
            label = "Study habits *",
            selected = state.studyHabits,
            options = StudyHabits.entries,
            display = { it.label },
            onSelect = viewModel::updateStudyHabits
        )

        Spacer(Modifier.height(12.dp))

        EnumDropdown(
            label = "Partying frequency *",
            selected = state.partyingFrequency,
            options = PartyingFrequency.entries.toList(),
            display = { it.label },
            onSelect = viewModel::updatePartying
        )

        /* -------------------------
           LIFESTYLE
        -------------------------- */
        SectionHeader("Lifestyle", onClear = viewModel::clearLifestyle)

        ToggleRow("Allow guests?", state.guestsAllowed, viewModel::updateGuests)
        ToggleRow("Allow overnight guests?", state.overnightGuests, viewModel::updateOvernight)
        ToggleRow("Are you a smoker?", state.smoker, viewModel::updateSmoker)
        ToggleRow("Do you drink?", state.drinker, viewModel::updateDrinker)
        ToggleRow("Pets?", state.pets, viewModel::updatePets)

        /* -------------------------
           HOUSING
        -------------------------- */
        SectionHeader("Housing Preferences", onClear = viewModel::clearHousing)

        OutlinedTextField(
            value = state.budgetRange,
            onValueChange = viewModel::updateBudget,
            label = { Text("Budget (e.g. \$700–\$900) *") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        EnumDropdown(
            label = "Room type preference *",
            selected = state.roomTypePreference,
            options = RoomTypePreference.entries.toList(),
            display = { it.label },
            onSelect = viewModel::updateRoomType
        )

        /* -------------------------
           SOCIAL
        -------------------------- */
        SectionHeader("Social Links", onClear = viewModel::clearSocial)

        OutlinedTextField(
            value = state.socialLinks,
            onValueChange = viewModel::updateSocialLinks,
            label = { Text("Contact / Social Media *") },
            modifier = Modifier.fillMaxWidth()
        )

        /* -------------------------
           ERROR MESSAGE
        -------------------------- */
        state.errorMsg?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        /* -------------------------
           SAVE
        -------------------------- */
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                // Only trigger save; toast is handled by LaunchedEffect
                viewModel.saveProfile()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving && !state.isDeleting
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Profile")
            }
        }

        Spacer(Modifier.height(12.dp))

        /* -------------------------
           SIGN OUT
        -------------------------- */
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isDeleting
        ) {
            Text("Sign Out")
        }

        Spacer(Modifier.height(12.dp))

        /* -------------------------
           DELETE ACCOUNT BUTTON
        -------------------------- */
        OutlinedButton(
            onClick = { showDeleteWarningDialog = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isDeleting && !state.isSaving,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Delete Account")
        }

        Spacer(Modifier.height(40.dp))
    }

    /* ---------------------------------------------------------
       DELETE ACCOUNT – STEP 1 (WARNING)
    --------------------------------------------------------- */
    if (showDeleteWarningDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteWarningDialog = false },
            title = { Text("Delete account?") },
            text = {
                Text(
                    "WARNING: This action is fatal!\n\n" +
                            "Are you sure that you want to permanently delete your account?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteWarningDialog = false
                        showPasswordDialog = true
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWarningDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    /* ---------------------------------------------------------
       DELETE ACCOUNT – STEP 2 (PASSWORD ENTRY)
    --------------------------------------------------------- */
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!state.isDeleting) showPasswordDialog = false
            },
            title = { Text("Confirm Account Deletion") },
            text = {
                Column {
                    Text("Enter your password to delete your account.")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.deletePassword,
                        onValueChange = viewModel::updateDeletePassword,
                        label = { Text("Password") },
                        singleLine = true
                    )
                    state.deleteError?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount() },
                    enabled = !state.isDeleting
                ) {
                    if (state.isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Continue", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPasswordDialog = false },
                    enabled = !state.isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ---------------------------------------------------------
   REUSABLE COMPONENTS
--------------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EnumDropdown(
    label: String,
    selected: T?,
    options: List<T>,
    display: (T) -> String,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selected?.let { display(it) } ?: "Select an option",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(display(item)) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SliderField(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Text(label)
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = 1f..5f,
        steps = 3
    )
    Spacer(Modifier.height(12.dp))
}
