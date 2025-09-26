package com.example.travelpractice.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

class PackingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
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
    
    // Packing Items
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

class TravelTasksRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    suspend fun getTravelTasks(): List<TravelTask> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            db.collection("travel_tasks")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(TravelTask::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addTravelTask(task: TravelTask): String {
        val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
        val taskWithUser = task.copy(userId = userId)
        return try {
            val docRef = db.collection("travel_tasks").add(taskWithUser).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add task: ${e.message}")
        }
    }
    
    suspend fun updateTravelTask(task: TravelTask) {
        try {
            val updatedTask = if (task.isCompleted && task.completedAt == null) {
                task.copy(completedAt = Date())
            } else {
                task
            }
            db.collection("travel_tasks")
                .document(task.id)
                .set(updatedTask)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update task: ${e.message}")
        }
    }
    
    suspend fun deleteTravelTask(taskId: String) {
        try {
            db.collection("travel_tasks")
                .document(taskId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to delete task: ${e.message}")
        }
    }
}

class ExpenseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    suspend fun getExpenses(): List<Expense> {
        val userId = getCurrentUserId() ?: return emptyList()
        return try {
            db.collection("expenses")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Expense::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addExpense(expense: Expense): String {
        val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
        val expenseWithUser = expense.copy(userId = userId)
        return try {
            val docRef = db.collection("expenses").add(expenseWithUser).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to add expense: ${e.message}")
        }
    }
    
    suspend fun updateExpense(expense: Expense) {
        try {
            db.collection("expenses")
                .document(expense.id)
                .set(expense)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update expense: ${e.message}")
        }
    }
    
    suspend fun deleteExpense(expenseId: String) {
        try {
            db.collection("expenses")
                .document(expenseId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to delete expense: ${e.message}")
        }
    }
    
    suspend fun getBudget(): Budget? {
        val userId = getCurrentUserId() ?: return null
        return try {
            val budgets = db.collection("budgets")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()
                .toObjects(Budget::class.java)
            budgets.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun saveBudget(budget: Budget): String {
        val userId = getCurrentUserId() ?: throw Exception("User not authenticated")
        val budgetWithUser = budget.copy(userId = userId)
        return try {
            val docRef = db.collection("budgets").add(budgetWithUser).await()
            docRef.id
        } catch (e: Exception) {
            throw Exception("Failed to save budget: ${e.message}")
        }
    }
}
