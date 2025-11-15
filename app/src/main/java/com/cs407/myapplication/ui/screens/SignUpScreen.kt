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
import androidx.compose.animation.animateContentSize
import androidx.navigation.NavController
import com.cs407.myapplication.ui.auth.AuthManager
import com.cs407.myapplication.ui.auth.isValidEmail
import com.cs407.myapplication.ui.auth.isStrongPassword
import com.cs407.myapplication.ui.auth.isWiscEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMsg by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val isSubmitAllowed = email.isNotBlank() || password.isNotBlank() || confirmPassword.isNotBlank()

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

                Text("Create an Account", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Join MadisonMapple today!",
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
                    label = { Text("Email (@wisc.edu)") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
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
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMsg = ""
                    },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(
                            onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                        ) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (isConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    modifier = Modifier.fillMaxWidth(),
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

                // SIGN UP BUTTON
                Button(
                    onClick = {
                        when {
                            !isValidEmail(email) -> {
                                errorMsg = "Please enter a valid email address."
                                return@Button
                            }
                            !isWiscEmail(email) -> {
                                errorMsg = "You must use your @wisc.edu email to sign up."
                                return@Button
                            }
                            !isStrongPassword(password) -> {
                                errorMsg =
                                    "Password must contain a minimum of 8 characters, including an uppercase letter, a digit, and a special character."
                                return@Button
                            }
                            password != confirmPassword -> {
                                errorMsg = "Your passwords do not match."
                                return@Button
                            }
                            else -> {
                                AuthManager.auth
                                    .createUserWithEmailAndPassword(email.trim(), password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = AuthManager.auth.currentUser
                                            user?.sendEmailVerification()

                                            // Immediately log them out; they must verify first
                                            AuthManager.auth.signOut()

                                            Toast.makeText(
                                                context,
                                                "Verification email sent! Please check your inbox.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            navController.navigate("login") {
                                                popUpTo("signUp") { inclusive = true }
                                            }
                                        } else {
                                            errorMsg = "That email is already in use."
                                        }
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isSubmitAllowed
                ) {
                    Text("Create Account")
                }

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already have an account? Log In")
                }
            }
        }
    }
}
