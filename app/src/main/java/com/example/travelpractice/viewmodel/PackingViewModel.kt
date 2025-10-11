package com.example.travelpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelpractice.data.PackingCategory
import com.example.travelpractice.data.PackingItem
import com.example.travelpractice.data.PackingRepository
// Removed sync service imports - using direct repository calls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PackingViewModel : ViewModel() {
    private val repository = PackingRepository()
    
    private val _categories = MutableStateFlow<List<PackingCategory>>(emptyList())
    val categories: StateFlow<List<PackingCategory>> = _categories.asStateFlow()
    
    private val _items = MutableStateFlow<List<PackingItem>>(emptyList())
    val items: StateFlow<List<PackingItem>> = _items.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load categories and items separately
                val loadedCategories = repository.getPackingCategories()
                val loadedItems = repository.getPackingItems()
                
                println("DEBUG: Loaded ${loadedCategories.size} categories from Firestore")
                loadedCategories.forEach { category ->
                    println("DEBUG: Loaded category: ${category.name}, ID: ${category.id}")
                }
                
                _categories.value = loadedCategories
                _items.value = loadedItems
                _isOffline.value = false
                
                // If no categories exist, create default ones
                if (_categories.value.isEmpty()) {
                    println("DEBUG: No categories found, creating default ones")
                    createDefaultCategories()
                }
                
                // If no items exist, create default ones
                if (_items.value.isEmpty()) {
                    createDefaultItems()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isOffline.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Removed loadStatistics - focusing on core functionality
    
    fun createDefaultCategories() {
        viewModelScope.launch {
            try {
                val defaultCategories = listOf(
                    PackingCategory(name = "Toiletries", color = "#FF6200EE", default = true),
                    PackingCategory(name = "Clothing", color = "#FF03DAC5", default = true),
                    PackingCategory(name = "Electronics", color = "#FF6200EE", default = true),
                    PackingCategory(name = "Travel Essentials", color = "#FF03DAC5", default = true),
                    PackingCategory(name = "Documents", color = "#FF6200EE", default = true)
                )
                
                defaultCategories.forEach { category ->
                    addCategory(category)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create default categories: ${e.message}"
            }
        }
    }
    
    fun createDefaultItems() {
        viewModelScope.launch {
            try {
                // Get categories first
                val categories = _categories.value
                if (categories.isEmpty()) return@launch
                
                // Create 2 items per category
                val defaultItems = listOf(
                    // Toiletries
                    PackingItem(name = "Toothbrush", categoryId = categories.find { it.name == "Toiletries" }?.id ?: "", categoryName = "Toiletries"),
                    PackingItem(name = "Toothpaste", categoryId = categories.find { it.name == "Toiletries" }?.id ?: "", categoryName = "Toiletries"),
                    
                    // Clothing
                    PackingItem(name = "Underwear", categoryId = categories.find { it.name == "Clothing" }?.id ?: "", categoryName = "Clothing"),
                    PackingItem(name = "Socks", categoryId = categories.find { it.name == "Clothing" }?.id ?: "", categoryName = "Clothing"),
                    
                    // Electronics
                    PackingItem(name = "Phone", categoryId = categories.find { it.name == "Electronics" }?.id ?: "", categoryName = "Electronics"),
                    PackingItem(name = "Charger", categoryId = categories.find { it.name == "Electronics" }?.id ?: "", categoryName = "Electronics"),
                    
                    // Travel Essentials
                    PackingItem(name = "Passport", categoryId = categories.find { it.name == "Travel Essentials" }?.id ?: "", categoryName = "Travel Essentials"),
                    PackingItem(name = "Tickets", categoryId = categories.find { it.name == "Travel Essentials" }?.id ?: "", categoryName = "Travel Essentials"),
                    
                    // Documents
                    PackingItem(name = "Travel Insurance", categoryId = categories.find { it.name == "Documents" }?.id ?: "", categoryName = "Documents"),
                    PackingItem(name = "Hotel Reservations", categoryId = categories.find { it.name == "Documents" }?.id ?: "", categoryName = "Documents")
                )
                
                defaultItems.forEach { item ->
                    addItem(item)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create default items: ${e.message}"
            }
        }
    }
    
    fun addCategory(category: PackingCategory) {
        viewModelScope.launch {
            try {
                println("DEBUG: Adding category: ${category.name}")
                val categoryId = repository.addPackingCategory(category)
                println("DEBUG: Category added with ID: $categoryId")
                // Update the local state immediately
                val updatedCategory = category.copy(id = categoryId)
                _categories.value = _categories.value + updatedCategory
                println("DEBUG: Categories count after adding: ${_categories.value.size}")
                println("DEBUG: Categories list: ${_categories.value.map { it.name }}")
            } catch (e: Exception) {
                println("DEBUG: Error adding category: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateCategory(category: PackingCategory) {
        viewModelScope.launch {
            try {
                repository.updatePackingCategory(category)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                repository.deletePackingCategory(categoryId)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun addItem(item: PackingItem) {
        viewModelScope.launch {
            try {
                println("DEBUG: Adding item: ${item.name} to category: ${item.categoryName}")
                val itemId = repository.addPackingItem(item)
                println("DEBUG: Item added with ID: $itemId")
                // Update the local state immediately
                val updatedItem = item.copy(id = itemId)
                _items.value = _items.value + updatedItem
                println("DEBUG: Items count after adding: ${_items.value.size}")
            } catch (e: Exception) {
                println("DEBUG: Error adding item: ${e.message}")
                _errorMessage.value = e.message
            }
        }
    }
    
    fun updateItem(item: PackingItem) {
        viewModelScope.launch {
            try {
                repository.updatePackingItem(item)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                repository.deletePackingItem(itemId)
                loadData()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun getItemsByCategory(categoryId: String): List<PackingItem> {
        return _items.value.filter { it.categoryId == categoryId }
    }
    
    fun getRemainingItems(): List<PackingItem> {
        return _items.value.filter { !it.isChecked }
    }
}
