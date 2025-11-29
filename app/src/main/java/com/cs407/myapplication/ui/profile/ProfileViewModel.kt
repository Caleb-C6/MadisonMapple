package com.cs407.myapplication.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.myapplication.data.profile.ProfileRepository
import com.cs407.myapplication.ui.auth.AuthManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

/* ---------------------------------------------------------
   VIEWMODEL
--------------------------------------------------------- */
class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val uid = AuthManager.auth.currentUser?.uid

    init {
        loadProfile()
    }

    /* -----------------------------
       LOAD PROFILE FROM FIRESTORE
    ----------------------------- */
    fun loadProfile() {
        if (uid == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMsg = "No logged-in user."
            )
            return
        }

        viewModelScope.launch {
            try {
                val profile = ProfileRepository.loadUserProfile(uid)

                if (profile != null) {
                    _uiState.value = _uiState.value.copy(
                        displayName = profile.displayName,
                        hometown = profile.hometown,
                        interests = profile.interests,
                        socialLinks = profile.socialLinks,

                        wakeUpTime = profile.wakeUpTime,
                        sleepTime = profile.sleepTime,
                        cleanliness = profile.cleanliness.toFloat(),
                        noiseTolerance = profile.noiseTolerance.toFloat(),
                        introvertExtrovert = profile.introvertExtrovert.toFloat(),
                        studyHabits = profile.studyHabits,
                        partyingFrequency = profile.partyingFrequency,

                        guestsAllowed = profile.guestsAllowed,
                        overnightGuests = profile.overnightGuests,
                        smoker = profile.smoker,
                        drinker = profile.drinker,
                        pets = profile.pets,

                        budgetRange = profile.budgetRange,
                        roomTypePreference = profile.roomTypePreference,
                        isLoading = false,
                        errorMsg = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Failed to load profile."
                )
            }
        }
    }

    /* -----------------------
       UI >>> STATE UPDATERS
    ------------------------ */
    fun updateDisplayName(v: String) { _uiState.value = _uiState.value.copy(displayName = v) }
    fun updateHometown(v: String) { _uiState.value = _uiState.value.copy(hometown = v) }
    fun updateInterests(v: String) { _uiState.value = _uiState.value.copy(interests = v) }
    fun updateSocialLinks(v: String) { _uiState.value = _uiState.value.copy(socialLinks = v) }

    fun updateWakeUp(v: WakeUpTime?) { _uiState.value = _uiState.value.copy(wakeUpTime = v) }
    fun updateSleep(v: SleepTime?) { _uiState.value = _uiState.value.copy(sleepTime = v) }
    fun updateStudyHabits(v: StudyHabits?) { _uiState.value = _uiState.value.copy(studyHabits = v) }
    fun updatePartying(v: PartyingFrequency?) { _uiState.value = _uiState.value.copy(partyingFrequency = v) }
    fun updateRoomType(v: RoomTypePreference?) { _uiState.value = _uiState.value.copy(roomTypePreference = v) }

    fun updateCleanliness(v: Float) { _uiState.value = _uiState.value.copy(cleanliness = v) }
    fun updateNoise(v: Float) { _uiState.value = _uiState.value.copy(noiseTolerance = v) }
    fun updateIntrovert(v: Float) { _uiState.value = _uiState.value.copy(introvertExtrovert = v) }

    fun updateGuests(v: Boolean) { _uiState.value = _uiState.value.copy(guestsAllowed = v) }
    fun updateOvernight(v: Boolean) { _uiState.value = _uiState.value.copy(overnightGuests = v) }
    fun updateSmoker(v: Boolean) { _uiState.value = _uiState.value.copy(smoker = v) }
    fun updateDrinker(v: Boolean) { _uiState.value = _uiState.value.copy(drinker = v) }
    fun updatePets(v: Boolean) { _uiState.value = _uiState.value.copy(pets = v) }

    fun updateBudget(v: String) { _uiState.value = _uiState.value.copy(budgetRange = v) }

    fun updateDeletePassword(v: String) {
        _uiState.value = _uiState.value.copy(
            deletePassword = v,
            deleteError = null
        )
    }

    fun consumeDeleteCompleted() {
        _uiState.value = _uiState.value.copy(deleteCompleted = false)
    }

    /* -----------------------
       CLEAR SECTION HELPERS
    ------------------------ */
    fun clearBasicInfo() {
        _uiState.value = _uiState.value.copy(
            displayName = "",
            hometown = "",
            interests = "",
            socialLinks = ""
        )
    }

    fun clearDailyRoutine() {
        _uiState.value = _uiState.value.copy(
            wakeUpTime = null,
            sleepTime = null
        )
    }

    fun clearHabits() {
        _uiState.value = _uiState.value.copy(
            cleanliness = 3f,
            noiseTolerance = 3f,
            introvertExtrovert = 3f,
            studyHabits = null,
            partyingFrequency = null
        )
    }

    fun clearLifestyle() {
        _uiState.value = _uiState.value.copy(
            guestsAllowed = false,
            overnightGuests = false,
            smoker = false,
            drinker = false,
            pets = false
        )
    }

    fun clearHousing() {
        _uiState.value = _uiState.value.copy(
            budgetRange = "",
            roomTypePreference = null
        )
    }

    fun clearSocial() {
        _uiState.value = _uiState.value.copy(
            socialLinks = ""
        )
    }

    /* -----------------------
       SAVE PROFILE
    ------------------------ */
    fun saveProfile() {
        val state = _uiState.value

        // Validate required text fields
        if (state.displayName.isBlank() ||
            state.hometown.isBlank() ||
            state.interests.isBlank() ||
            state.socialLinks.isBlank() ||
            state.budgetRange.isBlank()
        ) {
            _uiState.value = state.copy(errorMsg = "Please complete all required text fields.")
            return
        }

        // Validate dropdowns
        if (state.wakeUpTime == null ||
            state.sleepTime == null ||
            state.studyHabits == null ||
            state.partyingFrequency == null ||
            state.roomTypePreference == null
        ) {
            _uiState.value = state.copy(errorMsg = "Please complete all dropdowns.")
            return
        }

        if (uid == null) {
            _uiState.value = state.copy(errorMsg = "Not logged in.")
            return
        }

        _uiState.value = state.copy(isSaving = true, errorMsg = null)

        viewModelScope.launch {
            try {
                val profile = UserProfile(
                    uid = uid,
                    displayName = state.displayName,
                    hometown = state.hometown,
                    interests = state.interests,
                    socialLinks = state.socialLinks,
                    wakeUpTime = state.wakeUpTime!!,
                    sleepTime = state.sleepTime!!,
                    cleanliness = state.cleanliness.roundToInt(),
                    noiseTolerance = state.noiseTolerance.roundToInt(),
                    introvertExtrovert = state.introvertExtrovert.roundToInt(),
                    studyHabits = state.studyHabits!!,
                    partyingFrequency = state.partyingFrequency!!,
                    guestsAllowed = state.guestsAllowed,
                    overnightGuests = state.overnightGuests,
                    smoker = state.smoker,
                    drinker = state.drinker,
                    pets = state.pets,
                    budgetRange = state.budgetRange,
                    roomTypePreference = state.roomTypePreference!!
                )

                ProfileRepository.saveUserProfile(profile)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMsg = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMsg = "Failed to save profile."
                )
            }
        }
    }

    /* ------------------------------------
       DELETE ACCOUNT (email + password)
    ------------------------------------ */
    fun deleteAccount() {
        val state = _uiState.value
        val user = AuthManager.auth.currentUser
        val email = user?.email

        if (user == null || email == null) {
            _uiState.value = state.copy(
                deleteError = "No logged-in user.",
                isDeleting = false
            )
            return
        }

        if (state.deletePassword.isBlank()) {
            _uiState.value = state.copy(
                deleteError = "Please enter your password."
            )
            return
        }

        _uiState.value = state.copy(
            isDeleting = true,
            deleteError = null
        )

        viewModelScope.launch {
            try {
                // 1) Re-authenticate with email + password
                val credential = EmailAuthProvider.getCredential(email, state.deletePassword)
                user.reauthenticate(credential).await()

                // 2) Delete Firestore profile document
                Firebase.firestore
                    .collection("users")
                    .document(user.uid)
                    .delete()
                    .await()

                // 3) Delete Firebase Auth user
                user.delete().await()

                // 4) Update UI state -> deletion completed
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    deletePassword = "",
                    deleteError = null,
                    deleteCompleted = true
                )

            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    deleteError = "Incorrect password. Please try again."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    deleteError = "Failed to delete account. Please try again."
                )
            }
        }
    }
}
