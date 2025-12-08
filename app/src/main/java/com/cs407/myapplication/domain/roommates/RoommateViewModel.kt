package com.cs407.myapplication.domain.roommates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.myapplication.data.roommates.RoommateRepository
import com.cs407.myapplication.ui.auth.AuthManager
import com.cs407.myapplication.ui.profile.UserProfile
import com.cs407.myapplication.ui.profile.isComplete
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RoommateUiState(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val allProfiles: List<UserProfile> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val showFavoritesOnly: Boolean = false,
    val isTogglingFavorite: Boolean = false
)

class RoommateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RoommateUiState())
    val uiState: StateFlow<RoommateUiState> = _uiState

    private val currentUid: String? = AuthManager.auth.currentUser?.uid

    init {
        refresh()
    }

    fun refresh() {
        val uid = currentUid
        if (uid == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMsg = "No logged-in user."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMsg = null)

            try {
                val profiles = RoommateRepository.loadOtherUserProfiles(uid)
                val favorites = RoommateRepository.loadFavoriteIds(uid)

                val completeProfiles = profiles.filter { it.isComplete() && it.uid != uid }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    allProfiles = completeProfiles,
                    favoriteIds = favorites,
                    errorMsg = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Failed to load roommates."
                )
            }
        }
    }

    fun toggleShowFavoritesOnly() {
        val current = _uiState.value
        _uiState.value = current.copy(showFavoritesOnly = !current.showFavoritesOnly)
    }

    fun toggleFavoriteFor(user: UserProfile) {
        val uid = currentUid ?: return
        val current = _uiState.value
        val currentlyFavorite = current.favoriteIds.contains(user.uid)

        // Optimistically update UI
        val newFavoriteIds = if (currentlyFavorite) {
            current.favoriteIds - user.uid
        } else {
            current.favoriteIds + user.uid
        }

        _uiState.value = current.copy(
            favoriteIds = newFavoriteIds,
            isTogglingFavorite = true,
            errorMsg = null
        )

        viewModelScope.launch {
            try {
                RoommateRepository.setFavorite(
                    currentUid = uid,
                    roommateUid = user.uid,
                    isFavorite = !currentlyFavorite
                )
            } catch (e: Exception) {
                // If Firestore fails, roll back to previous state
                _uiState.value = _uiState.value.copy(
                    favoriteIds = current.favoriteIds,
                    errorMsg = "Failed to update favorites."
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    isTogglingFavorite = false
                )
            }
        }
    }
}
