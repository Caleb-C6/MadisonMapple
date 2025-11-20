package com.cs407.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cs407.myapplication.ui.auth.AuthManager
import com.cs407.myapplication.ui.profile.ProfileRepository
import com.cs407.myapplication.ui.profile.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val currentUser = AuthManager.auth.currentUser
    val uid = currentUser?.uid

    // Local form state
    var displayName by remember { mutableStateOf("") }
    var hometown by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }
    var preferredLocations by remember { mutableStateOf("") }
    var socialLinks by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Load existing profile when screen opens
    LaunchedEffect(uid) {
        if (uid == null) {
            errorMsg = "No logged-in user."
            isLoading = false
            return@LaunchedEffect
        }

        val profile = ProfileRepository.loadUserProfile(uid)
        if (profile != null) {
            displayName = profile.displayName
            hometown = profile.hometown
            interests = profile.interests
            preferredLocations = profile.preferredLocations
            socialLinks = profile.socialLinks
        }
        isLoading = false
    }

    if (isLoading) {
        // Simple centered progress
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Your Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = currentUser?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // NAME
        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // HOMETOWN
        OutlinedTextField(
            value = hometown,
            onValueChange = { hometown = it },
            label = { Text("Hometown") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // INTERESTS / HOBBIES
        OutlinedTextField(
            value = interests,
            onValueChange = { interests = it },
            label = { Text("Interests & hobbies") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        // PREFERRED LOCATIONS
        OutlinedTextField(
            value = preferredLocations,
            onValueChange = { preferredLocations = it },
            label = { Text("Preferred locations for housing") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        // SOCIAL LINKS
        OutlinedTextField(
            value = socialLinks,
            onValueChange = { socialLinks = it },
            label = { Text("Social media / contact links") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 1
        )

        Spacer(Modifier.height(12.dp))

        if (errorMsg != null) {
            Text(
                text = errorMsg!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }

        // SAVE BUTTON
        Button(
            onClick = {
                if (uid == null) {
                    errorMsg = "No logged-in user."
                    return@Button
                }

                isSaving = true
                errorMsg = null

                val profile = UserProfile(
                    uid = uid,
                    displayName = displayName.trim(),
                    hometown = hometown.trim(),
                    interests = interests.trim(),
                    preferredLocations = preferredLocations.trim(),
                    socialLinks = socialLinks.trim()
                )

                scope.launch {
                    try {
                        ProfileRepository.saveUserProfile(profile)
                        Toast.makeText(
                            context,
                            "Profile saved.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        errorMsg = "Failed to save profile. Please try again."
                    } finally {
                        isSaving = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Profile")
            }
        }

        Spacer(Modifier.height(24.dp))

        // SIGN OUT BUTTON
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Out")
        }
    }
}
