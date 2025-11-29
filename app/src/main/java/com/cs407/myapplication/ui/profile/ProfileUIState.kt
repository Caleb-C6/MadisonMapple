package com.cs407.myapplication.ui.profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    // Delete state
    val isDeleting: Boolean = false,
    val deletePassword: String = "",
    val deleteError: String? = null,
    val deleteCompleted: Boolean = false,

    // Validation / general error
    val errorMsg: String? = null,

    // Basic information
    val displayName: String = "",
    val hometown: String = "",
    val interests: String = "",
    val socialLinks: String = "",

    // Daily routine & habits (nullable until a choice is selected)
    val wakeUpTime: WakeUpTime? = null,
    val sleepTime: SleepTime? = null,
    val cleanliness: Float = 3f,
    val noiseTolerance: Float = 3f,
    val introvertExtrovert: Float = 3f,
    val studyHabits: StudyHabits? = null,
    val partyingFrequency: PartyingFrequency? = null,

    // Lifestyle
    val guestsAllowed: Boolean = false,
    val overnightGuests: Boolean = false,
    val smoker: Boolean = false,
    val drinker: Boolean = false,
    val pets: Boolean = false,

    // Housing
    val budgetRange: String = "",
    val roomTypePreference: RoomTypePreference? = null
)
