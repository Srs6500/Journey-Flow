package com.example.travelpractice.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await
import java.util.Date

class ChecklistSyncService {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    // Sync all checklist data for a user
    suspend fun syncUserChecklistData(): SyncResult {
        val userId = getCurrentUserId() ?: return SyncResult.Error("User not authenticated")
        
        return try {
            // Get all categories and items for the user
            val categories = db.collection("packing_categories")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(PackingCategory::class.java)
            
            val items = db.collection("packing_items")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(PackingItem::class.java)
            
            SyncResult.Success(
                categories = categories,
                items = items
            )
        } catch (e: Exception) {
            SyncResult.Error("Failed to sync data: ${e.message}")
        }
    }
    
    // Batch update multiple items
    suspend fun batchUpdateItems(items: List<PackingItem>): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            val batch = db.batch()
            
            items.forEach { item ->
                val docRef = db.collection("packing_items").document(item.id)
                val updatedItem = item.copy(
                    userId = userId
                )
                batch.set(docRef, updatedItem)
            }
            
            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Batch update multiple categories
    suspend fun batchUpdateCategories(categories: List<PackingCategory>): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            val batch = db.batch()
            
            categories.forEach { category ->
                val docRef = db.collection("packing_categories").document(category.id)
                val updatedCategory = category.copy(
                    userId = userId
                )
                batch.set(docRef, updatedCategory)
            }
            
            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Create default packing list for new users
    suspend fun createDefaultPackingList(): Boolean {
        val userId = getCurrentUserId() ?: return false
        
        return try {
            val batch = db.batch()
            
            // Create default categories
            val defaultCategories = listOf(
                PackingCategory(
                    name = "Toiletries",
                    color = "#FF6200EE",
                    default = true,
                    userId = userId
                ),
                PackingCategory(
                    name = "Clothing",
                    color = "#FF03DAC5",
                    default = true,
                    userId = userId
                ),
                PackingCategory(
                    name = "Travel Essentials",
                    color = "#FF6200EE",
                    default = true,
                    userId = userId
                ),
                PackingCategory(
                    name = "Electronics",
                    color = "#FF03DAC5",
                    default = true,
                    userId = userId
                ),
                PackingCategory(
                    name = "Documents",
                    color = "#FF6200EE",
                    default = true,
                    userId = userId
                )
            )
            
            val categoryRefs = mutableListOf<String>()
            defaultCategories.forEach { category ->
                val docRef = db.collection("packing_categories").document()
                val categoryWithId = category.copy(id = docRef.id)
                batch.set(docRef, categoryWithId)
                categoryRefs.add(docRef.id)
            }
            
            // Create default items
            val defaultItems = listOf(
                // Toiletries
                PackingItem(name = "Toothbrush", categoryId = categoryRefs[0], categoryName = "Toiletries", userId = userId),
                PackingItem(name = "Toothpaste", categoryId = categoryRefs[0], categoryName = "Toiletries", userId = userId),
                PackingItem(name = "Shampoo", categoryId = categoryRefs[0], categoryName = "Toiletries", userId = userId),
                PackingItem(name = "Soap", categoryId = categoryRefs[0], categoryName = "Toiletries", userId = userId),
                PackingItem(name = "Deodorant", categoryId = categoryRefs[0], categoryName = "Toiletries", userId = userId),
                
                // Clothing
                PackingItem(name = "Underwear", categoryId = categoryRefs[1], categoryName = "Clothing", userId = userId),
                PackingItem(name = "Socks", categoryId = categoryRefs[1], categoryName = "Clothing", userId = userId),
                PackingItem(name = "T-shirts", categoryId = categoryRefs[1], categoryName = "Clothing", userId = userId),
                PackingItem(name = "Pants/Jeans", categoryId = categoryRefs[1], categoryName = "Clothing", userId = userId),
                PackingItem(name = "Pajamas", categoryId = categoryRefs[1], categoryName = "Clothing", userId = userId),
                
                // Travel Essentials
                PackingItem(name = "Passport/ID", categoryId = categoryRefs[2], categoryName = "Travel Essentials", userId = userId),
                PackingItem(name = "Tickets/Boarding Pass", categoryId = categoryRefs[2], categoryName = "Travel Essentials", userId = userId),
                PackingItem(name = "Wallet", categoryId = categoryRefs[2], categoryName = "Travel Essentials", userId = userId),
                PackingItem(name = "Phone Charger", categoryId = categoryRefs[2], categoryName = "Travel Essentials", userId = userId),
                PackingItem(name = "Headphones", categoryId = categoryRefs[2], categoryName = "Travel Essentials", userId = userId),
                
                // Electronics
                PackingItem(name = "Phone", categoryId = categoryRefs[3], categoryName = "Electronics", userId = userId),
                PackingItem(name = "Laptop/Tablet", categoryId = categoryRefs[3], categoryName = "Electronics", userId = userId),
                PackingItem(name = "Camera", categoryId = categoryRefs[3], categoryName = "Electronics", userId = userId),
                PackingItem(name = "Power Bank", categoryId = categoryRefs[3], categoryName = "Electronics", userId = userId),
                
                // Documents
                PackingItem(name = "Travel Insurance", categoryId = categoryRefs[4], categoryName = "Documents", userId = userId),
                PackingItem(name = "Hotel Reservations", categoryId = categoryRefs[4], categoryName = "Documents", userId = userId),
                PackingItem(name = "Emergency Contacts", categoryId = categoryRefs[4], categoryName = "Documents", userId = userId)
            )
            
            defaultItems.forEach { item ->
                val docRef = db.collection("packing_items").document()
                val itemWithId = item.copy(id = docRef.id)
                batch.set(docRef, itemWithId)
            }
            
            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Removed statistics - focusing on core functionality
}

sealed class SyncResult {
    data class Success(
        val categories: List<PackingCategory>,
        val items: List<PackingItem>
    ) : SyncResult()
    
    data class Error(val message: String) : SyncResult()
}
