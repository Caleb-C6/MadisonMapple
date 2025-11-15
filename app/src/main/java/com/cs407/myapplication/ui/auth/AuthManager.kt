package com.cs407.myapplication.ui.auth

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
}