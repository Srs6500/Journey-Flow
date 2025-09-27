package com.example.travelpractice.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class PackingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    init {
        // Enable offline persistence
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    // Packing Categories
    suspend fun getPackingCategories(): List<PackingCategory> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            db.collection("packing_categories")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(PackingCategory::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addPackingCategory(category: PackingCategory): String {
        val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
        val categoryWithUser = category.copy(userId = userId)
        return try {
            val docRef = db.collection("packing_categories").add(categoryWithUser).await()
            // Update the document with the correct ID
            val updatedCategory = categoryWithUser.copy(id = docRef.id)
            db.collection("packing_categories").document(docRef.id).set(updatedCategory).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add category: ${e.message}")
        }
    }
    
    suspend fun updatePackingCategory(category: PackingCategory) {
        try {
            db.collection("packing_categories")
                .document(category.id)
                .set(category)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update category: ${e.message}")
        }
    }
    
    suspend fun deletePackingCategory(categoryId: String) {
        try {
            // First delete all items in this category
            val items = getPackingItemsByCategory(categoryId)
            items.forEach { item ->
                deletePackingItem(item.id)
            }
            
            // Then delete the category
            db.collection("packing_categories")
                .document(categoryId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to delete category: ${e.message}")
        }
    }
    
    // Packing Items - Methods for packing_items collection
    suspend fun getPackingItems(): List<PackingItem> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            db.collection("packing_items")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(PackingItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getPackingItemsByCategory(categoryId: String): List<PackingItem> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            db.collection("packing_items")
                .whereEqualTo("userId", userId)
                .whereEqualTo("categoryId", categoryId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(PackingItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addPackingItem(item: PackingItem): String {
        val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
        val itemWithUser = item.copy(userId = userId)
        return try {
            val docRef = db.collection("packing_items").add(itemWithUser).await()
            // Update the document with the correct ID
            val updatedItem = itemWithUser.copy(id = docRef.id)
            db.collection("packing_items").document(docRef.id).set(updatedItem).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add item: ${e.message}")
        }
    }
    
    suspend fun updatePackingItem(item: PackingItem) {
        try {
            db.collection("packing_items")
                .document(item.id)
                .set(item)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update item: ${e.message}")
        }
    }
    
    suspend fun deletePackingItem(itemId: String) {
        try {
            db.collection("packing_items")
                .document(itemId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to delete item: ${e.message}")
        }
    }
}

// TravelTasksRepository REMOVED - focusing only on checklist feature

// ExpenseRepository REMOVED - focusing only on checklist feature
