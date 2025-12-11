package com.cs407.myapplication.ui.apartments

object ApartmentNameMapper {
    // Map only display names that differ from database names
    private val nameMap = mapOf(
        "Waterfront Apartment" to "Waterfront",
        "Palisade Properties" to "Palisade",
        "140 Iota Courts" to "Iota Courts",
        "The Langdon Apartment" to "Langdon"
    )

    fun getDatabaseName(displayName: String): String {
        return nameMap[displayName] ?: displayName
    }

    fun getDisplayName(databaseName: String): String {
        return nameMap.entries.find { it.value == databaseName }?.key ?: databaseName
    }

    fun getAllDisplayNames(): List<String> = nameMap.keys.toList()

    fun getAllDatabaseNames(): List<String> = nameMap.values.toList()
}
