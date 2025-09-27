package com.example.travelpractice.data

import java.util.Date

// Packing List Models - Updated to match exact Firestore structure
data class PackingCategory(
    val id: String = "", // Document ID from Firestore
    val name: String = "", // e.g., "Toiletries", "Clothing"
    val color: String = "#FF6200EE", // e.g., "#FF6200EE", "#FF03DAC5"
    val userId: String = "", // e.g., "cfz9fc6mLTRaDQXNMYKPUgeaV4V2"
    val createdAt: Date = Date(), // Timestamp
    val default: Boolean = false // e.g., true, false
)

data class PackingItem(
    val id: String = "", // Document ID from Firestore
    val name: String = "", // e.g., "Toothbrush", "Socks"
    val categoryId: String = "", // References PackingCategory.id
    val categoryName: String = "", // e.g., "Toiletries", "Clothing" (for easy display)
    val isChecked: Boolean = false, // Checkbox status
    val userId: String = "", // e.g., "cfz9fc6mLTRaDQXNMYKPUgeaV4V2"
    val createdAt: Date = Date() // Timestamp
)

// Travel Tasks Models REMOVED - focusing only on checklist feature

// Expense Tracking Models REMOVED - focusing only on checklist feature
