package com.cs407.myapplication.ui.profile

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object ProfileRepository {

    private const val USERS_COLLECTION = "users"

    @SuppressLint("StaticFieldLeak")
    private val firestore = Firebase.firestore

    @SuppressLint("NewApi")
    suspend fun loadUserProfile(uid: String): UserProfile? {
        return try {
            val snapshot = firestore
                .collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        firestore
            .collection(USERS_COLLECTION)
            .document(profile.uid)
            .set(profile)
            .await()
    }
}
