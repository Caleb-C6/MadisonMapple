package com.cs407.myapplication.ui.apartments

object ApartmentNameMapper {
    // Map display names (from your UI) to database names
    private val nameMap = mapOf(
        "Waterfront Apartment" to "Waterfront",
        "Palisade Properties" to "Palisade",
        "Aberdeen Apartments" to "Aberdeen Apartments",
        "140 Iota Courts" to "Iota Courts",
        "The Langdon Apartment" to "Langdon"
    )

    // Convert display name to database name
    fun getDatabaseName(displayName: String): String {
        return nameMap[displayName] ?: displayName
    }

    // Optional: Convert database name to display name
    fun getDisplayName(databaseName: String): String {
        return nameMap.entries.find { it.value == databaseName }?.key ?: databaseName
    }

    // Get all display names (for your UI list)
    fun getAllDisplayNames(): List<String> = nameMap.keys.toList()

    // Get all database names (for queries)
    fun getAllDatabaseNames(): List<String> = nameMap.values.toList()
}