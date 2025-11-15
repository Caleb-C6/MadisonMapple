package com.cs407.myapplication.ui.auth

import android.util.Patterns

// Basic email syntax check
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
}

// Strong password: length + complexity
fun isStrongPassword(password: String): Boolean {
    return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isDigit() } &&
            password.any { "!@#$%^&*()-_=+[{]}|;:'\",<.>/?".contains(it) }
}

// Require UW–Madison student email
fun isWiscEmail(email: String): Boolean {
    return email.trim().lowercase().endsWith("@wisc.edu")
}
