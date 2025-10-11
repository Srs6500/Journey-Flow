package com.example.travelpractice.repository

import com.example.travelpractice.data.PackingCategory
import com.example.travelpractice.data.PackingItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class PackingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    fun getCurrentUserId(): String {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            throw Exception("User not authenticated - please sign in again")
        }
        return userId
    }
    
    // Categories Collection: users/{userId}/packingCategories/{categoryId}
    private fun getCategoriesCollection() = 
        db.collection("users").document(getCurrentUserId()).collection("packingCategories")
    
    // Items Collection: users/{userId}/packingItems/{itemId}
    private fun getItemsCollection() = 
        db.collection("users").document(getCurrentUserId()).collection("packingItems")
    
    // Categories CRUD Operations
    suspend fun getPackingCategories(): List<PackingCategory> {
        return try {
            val snapshot = getCategoriesCollection()
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(PackingCategory::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch categories: ${e.message}")
        }
    }
    
    suspend fun addPackingCategory(category: PackingCategory): String {
        return try {
            val categoryData = category.copy(
                userId = getCurrentUserId(),
                createdAt = Date()
            )
            
            val docRef = getCategoriesCollection().add(categoryData).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add category: ${e.message}")
        }
    }
    
    suspend fun updatePackingCategory(category: PackingCategory) {
        try {
            getCategoriesCollection().document(category.id).set(category).await()
        } catch (e: Exception) {
            throw Exception("Failed to update category: ${e.message}")
        }
    }
    
    suspend fun deletePackingCategory(categoryId: String) {
        try {
            getCategoriesCollection().document(categoryId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete category: ${e.message}")
        }
    }
    
    // Items CRUD Operations
    suspend fun getPackingItems(): List<PackingItem> {
        return try {
            val snapshot = getItemsCollection()
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(PackingItem::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch items: ${e.message}")
        }
    }
    
    suspend fun addPackingItem(item: PackingItem): String {
        return try {
            val itemData = item.copy(
                userId = getCurrentUserId(),
                createdAt = Date()
            )
            
            val docRef = getItemsCollection().add(itemData).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add item: ${e.message}")
        }
    }
    
    suspend fun updatePackingItem(item: PackingItem) {
        try {
            getItemsCollection().document(item.id).set(item).await()
        } catch (e: Exception) {
            throw Exception("Failed to update item: ${e.message}")
        }
    }
    
    suspend fun deletePackingItem(itemId: String) {
        try {
            getItemsCollection().document(itemId).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete item: ${e.message}")
        }
    }
    
    // Get items by category
    suspend fun getItemsByCategory(categoryId: String): List<PackingItem> {
        return try {
            val snapshot = getItemsCollection()
                .whereEqualTo("categoryId", categoryId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(PackingItem::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch items by category: ${e.message}")
        }
    }
}
