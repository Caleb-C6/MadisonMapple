package com.cs407.myapplication.ui.profile

enum class WakeUpTime(val label: String) {
    EARLY("Before 7 AM"),
    NORMAL("7–9 AM"),
    LATE("After 9 AM")
}

enum class SleepTime(val label: String) {
    EARLY("Before 10 PM"),
    NORMAL("10 PM–12 AM"),
    LATE("After midnight")
}

enum class StudyHabits(val label: String) {
    QUIET("Alone"),
    GROUP("Group study"),
    FLEXIBLE("Flexible / mixed")
}

enum class PartyingFrequency(val label: String) {
    RARELY("Rarely"),
    SOMETIMES("Some weekends"),
    OFTEN("Most weekends")
}

enum class RoomTypePreference(val label: String) {
    SINGLE("Single room"),
    DOUBLE("Shared room (2)"),
    TRIPLE("3+ roommates"),
    NO_PREF("No strong preference")
}

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val hometown: String = "",
    val interests: String = "",
    val socialLinks: String = "",

    val wakeUpTime: WakeUpTime? = null,
    val sleepTime: SleepTime? = null,
    val cleanliness: Int = 3,
    val noiseTolerance: Int = 3,
    val introvertExtrovert: Int = 3,
    val studyHabits: StudyHabits? = null,
    val partyingFrequency: PartyingFrequency? = null,

    val guestsAllowed: Boolean = false,
    val overnightGuests: Boolean = false,
    val smoker: Boolean = false,
    val drinker: Boolean = false,
    val pets: Boolean = false,

    val budgetRange: String = "",
    val roomTypePreference: RoomTypePreference? = null,

    val favoriteUids: List<String> = emptyList()
)

fun UserProfile.isComplete(): Boolean {
    return displayName.isNotBlank() &&
            hometown.isNotBlank() &&
            interests.isNotBlank() &&
            socialLinks.isNotBlank() &&
            budgetRange.isNotBlank() &&
            wakeUpTime != null &&
            sleepTime != null &&
            studyHabits != null &&
            partyingFrequency != null &&
            roomTypePreference != null
}
