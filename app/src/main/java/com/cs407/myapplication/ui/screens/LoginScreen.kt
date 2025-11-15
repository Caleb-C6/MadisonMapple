package com.cs407.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.animation.animateContentSize
import com.cs407.myapplication.ui.auth.AuthManager
import com.cs407.myapplication.ui.auth.isValidEmail
import com.cs407.myapplication.ui.auth.isWiscEmail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // "Forgot Password?" dialog state
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val isSubmitAllowed = email.isNotBlank() || password.isNotBlank()

    fun sendPasswordReset(email: String) {
        AuthManager.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "An email has been sent! Please check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Failed to send reset email. Please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Close dialog regardless of outcome
                showResetDialog = false
                resetEmail = ""
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Log in to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                // EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMsg = ""
                    },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMsg.contains("email", ignoreCase = true),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMsg = ""
                    },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMsg.contains("password", ignoreCase = true),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                if (errorMsg.isNotEmpty()) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // LOGIN BUTTON
                Button(
                    onClick = {
                        // Basic validation
                        if (email.isBlank()) {
                            errorMsg = "Please enter your email."
                            return@Button
                        }
                        if (!isValidEmail(email)) {
                            errorMsg = "Please enter a valid email address."
                            return@Button
                        }
                        if (!isWiscEmail(email)) {
                            errorMsg = "You must use your @wisc.edu email to log in."
                            return@Button
                        }
                        if (password.isBlank()) {
                            errorMsg = "Please enter your password."
                            return@Button
                        }

                        isLoading = true
                        errorMsg = ""

                        scope.launch {
                            AuthManager.auth
                                .signInWithEmailAndPassword(email.trim(), password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        val user = AuthManager.auth.currentUser
                                        // Require verified @wisc.edu account
                                        if (user == null ||
                                            user.email.isNullOrBlank() ||
                                            !user.email!!.lowercase().endsWith("@wisc.edu") ||
                                            !user.isEmailVerified
                                        ) {
                                            AuthManager.auth.signOut()
                                            errorMsg =
                                                "Please verify your @wisc.edu email before logging in. Check your inbox and spam folder."
                                        } else {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    } else {
                                        errorMsg = "Incorrect email or password."
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSubmitAllowed && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }

                Spacer(Modifier.height(12.dp))

                FilledTonalButton(
                    onClick = { navController.navigate("signUp") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Don't have an account? Sign Up")
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = { showResetDialog = true }) {
                    Text("Forgot Password?")
                }
            }
        }
    }

    // FORGOT PASSWORD DIALOG
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Password") },
            text = {
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it },
                    label = { Text("Enter your @wisc.edu email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isValidEmail(resetEmail) || !isWiscEmail(resetEmail)) {
                            Toast.makeText(
                                context,
                                "Please enter a valid @wisc.edu email.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            sendPasswordReset(resetEmail.trim())
                        }
                    }
                ) {
                    Text("Send Email")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
