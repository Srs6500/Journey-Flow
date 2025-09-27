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
            
            // Travel tasks removed - focusing only on checklist
            
            // Expenses and budgets removed - focusing only on checklist
            
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
                            put("default", category.default)
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
                        })
                    }
                })
                
                // Tasks data - REMOVED (focusing only on checklist)
                put("travelTasks", JSONArray())
                
                // Expenses data - REMOVED (focusing only on checklist)
                put("expenses", JSONArray())
                
                // Budget data - REMOVED (focusing only on checklist)
                put("budgets", JSONArray())
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
                    default = categoryObj.getBoolean("default")
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
                    createdAt = Date(itemObj.getLong("createdAt"))
                )
                db.collection("packing_items").add(item).await()
            }
            
            // Import travel tasks - REMOVED (focusing only on checklist)
            
            // Import expenses - REMOVED (focusing only on checklist)
            
            // Import budgets - REMOVED (focusing only on checklist)
            
            return true
            
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
