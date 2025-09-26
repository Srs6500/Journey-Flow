package com.example.travelpractice.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.travelpractice.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class DataExportHelper(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    suspend fun exportAllData(): Uri? {
        val userId = auth.currentUser?.uid ?: return null
        
        try {
            // Fetch all data
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
            
            val tasks = db.collection("travel_tasks")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(TravelTask::class.java)
            
            val expenses = db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(Expense::class.java)
            
            val budgets = db.collection("budgets")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects(Budget::class.java)
            
            // Create JSON export
            val exportData = JSONObject().apply {
                put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                put("appVersion", "1.0.0")
                put("userId", userId)
                
                // Packing data
                put("packingCategories", JSONArray().apply {
                    categories.forEach { category ->
                        put(JSONObject().apply {
                            put("id", category.id)
                            put("name", category.name)
                            put("color", category.color)
                            put("createdAt", category.createdAt.time)
                            put("isDefault", category.isDefault)
                        })
                    }
                })
                
                put("packingItems", JSONArray().apply {
                    items.forEach { item ->
                        put(JSONObject().apply {
                            put("id", item.id)
                            put("name", item.name)
                            put("categoryId", item.categoryId)
                            put("isChecked", item.isChecked)
                            put("createdAt", item.createdAt.time)
                            put("isDefault", item.isDefault)
                        })
                    }
                })
                
                // Tasks data
                put("travelTasks", JSONArray().apply {
                    tasks.forEach { task ->
                        put(JSONObject().apply {
                            put("id", task.id)
                            put("title", task.title)
                            put("description", task.description)
                            put("category", task.category.name)
                            put("dueDate", task.dueDate?.time)
                            put("isCompleted", task.isCompleted)
                            put("priority", task.priority.name)
                            put("createdAt", task.createdAt.time)
                            put("completedAt", task.completedAt?.time)
                        })
                    }
                })
                
                // Expenses data
                put("expenses", JSONArray().apply {
                    expenses.forEach { expense ->
                        put(JSONObject().apply {
                            put("id", expense.id)
                            put("amount", expense.amount)
                            put("description", expense.description)
                            put("category", expense.category.name)
                            put("date", expense.date.time)
                            put("createdAt", expense.createdAt.time)
                        })
                    }
                })
                
                // Budget data
                put("budgets", JSONArray().apply {
                    budgets.forEach { budget ->
                        put(JSONObject().apply {
                            put("id", budget.id)
                            put("totalBudget", budget.totalBudget)
                            put("dailyBudget", budget.dailyBudget)
                            put("spentAmount", budget.spentAmount)
                            put("startDate", budget.startDate.time)
                            put("endDate", budget.endDate.time)
                            put("createdAt", budget.createdAt.time)
                        })
                    }
                })
            }
            
            // Save to file
            val fileName = "journeyflow_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
            val file = File(context.getExternalFilesDir(null), fileName)
            val fileWriter = FileWriter(file)
            fileWriter.write(exportData.toString(2))
            fileWriter.close()
            
            // Return URI for sharing
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    fun shareExportFile(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Export JourneyFlow Data")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
    
    suspend fun importData(jsonString: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        
        try {
            val importData = JSONObject(jsonString)
            
            // Import packing categories
            val categoriesArray = importData.getJSONArray("packingCategories")
            for (i in 0 until categoriesArray.length()) {
                val categoryObj = categoriesArray.getJSONObject(i)
                val category = PackingCategory(
                    name = categoryObj.getString("name"),
                    color = categoryObj.getString("color"),
                    userId = userId,
                    createdAt = Date(categoryObj.getLong("createdAt")),
                    isDefault = categoryObj.getBoolean("isDefault")
                )
                db.collection("packing_categories").add(category).await()
            }
            
            // Import packing items
            val itemsArray = importData.getJSONArray("packingItems")
            for (i in 0 until itemsArray.length()) {
                val itemObj = itemsArray.getJSONObject(i)
                val item = PackingItem(
                    name = itemObj.getString("name"),
                    categoryId = itemObj.getString("categoryId"),
                    isChecked = itemObj.getBoolean("isChecked"),
                    userId = userId,
                    createdAt = Date(itemObj.getLong("createdAt")),
                    isDefault = itemObj.getBoolean("isDefault")
                )
                db.collection("packing_items").add(item).await()
            }
            
            // Import travel tasks
            val tasksArray = importData.getJSONArray("travelTasks")
            for (i in 0 until tasksArray.length()) {
                val taskObj = tasksArray.getJSONObject(i)
                val task = TravelTask(
                    title = taskObj.getString("title"),
                    description = taskObj.getString("description"),
                    category = TaskCategory.valueOf(taskObj.getString("category")),
                    dueDate = if (taskObj.has("dueDate") && !taskObj.isNull("dueDate")) {
                        Date(taskObj.getLong("dueDate"))
                    } else null,
                    isCompleted = taskObj.getBoolean("isCompleted"),
                    priority = TaskPriority.valueOf(taskObj.getString("priority")),
                    userId = userId,
                    createdAt = Date(taskObj.getLong("createdAt")),
                    completedAt = if (taskObj.has("completedAt") && !taskObj.isNull("completedAt")) {
                        Date(taskObj.getLong("completedAt"))
                    } else null
                )
                db.collection("travel_tasks").add(task).await()
            }
            
            // Import expenses
            val expensesArray = importData.getJSONArray("expenses")
            for (i in 0 until expensesArray.length()) {
                val expenseObj = expensesArray.getJSONObject(i)
                val expense = Expense(
                    amount = expenseObj.getDouble("amount"),
                    description = expenseObj.getString("description"),
                    category = ExpenseCategory.valueOf(expenseObj.getString("category")),
                    date = Date(expenseObj.getLong("date")),
                    userId = userId,
                    createdAt = Date(expenseObj.getLong("createdAt"))
                )
                db.collection("expenses").add(expense).await()
            }
            
            // Import budgets
            val budgetsArray = importData.getJSONArray("budgets")
            for (i in 0 until budgetsArray.length()) {
                val budgetObj = budgetsArray.getJSONObject(i)
                val budget = Budget(
                    totalBudget = budgetObj.getDouble("totalBudget"),
                    dailyBudget = budgetObj.getDouble("dailyBudget"),
                    spentAmount = budgetObj.getDouble("spentAmount"),
                    startDate = Date(budgetObj.getLong("startDate")),
                    endDate = Date(budgetObj.getLong("endDate")),
                    userId = userId,
                    createdAt = Date(budgetObj.getLong("createdAt"))
                )
                db.collection("budgets").add(budget).await()
            }
            
            return true
            
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
