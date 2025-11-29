package com.cs407.myapplication.data.roommates

import android.annotation.SuppressLint
import com.cs407.myapplication.ui.profile.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object RoommateRepository {

    private const val USERS_COLLECTION = "users"
    private const val FAVORITES_SUBCOLLECTION = "favorites"

    @SuppressLint("StaticFieldLeak")
    private val firestore = Firebase.firestore

    /**
     * Load the profiles of every user, filtering out:
     *   - the current user
     *   - incomplete profiles
     */
    suspend fun loadOtherUserProfiles(currentUid: String): List<UserProfile> {
        val snapshot = firestore
            .collection(USERS_COLLECTION)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val profile = doc.toObject(UserProfile::class.java)
            profile?.copy(uid = doc.id)
        }.filter { profile ->
            profile.uid != currentUid && isProfileComplete(profile)
        }
    }

    private fun isProfileComplete(p: UserProfile): Boolean {
        return p.displayName.isNotBlank() &&
                p.hometown.isNotBlank() &&
                p.interests.isNotBlank() &&
                p.socialLinks.isNotBlank() &&
                p.budgetRange.isNotBlank()
    }

    /**
     * Load the set of user IDs that the current user has "hearted".
     */
    suspend fun loadFavoriteIds(currentUid: String): Set<String> {
        val snapshot = firestore
            .collection(USERS_COLLECTION)
            .document(currentUid)
            .collection(FAVORITES_SUBCOLLECTION)
            .get()
            .await()

        return snapshot.documents.map { it.id }.toSet()
    }

    /**
     * Mark or unmark [roommateUid] as a favorite of [currentUid].
     */
    suspend fun setFavorite(
        currentUid: String,
        roommateUid: String,
        isFavorite: Boolean
    ) {
        val docRef = firestore
            .collection(USERS_COLLECTION)
            .document(currentUid)
            .collection(FAVORITES_SUBCOLLECTION)
            .document(roommateUid)

        if (isFavorite) {
            docRef.set(mapOf("favorite" to true)).await()
        } else {
            docRef.delete().await()
        }
    }
}