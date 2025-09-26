package com.example.travelpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelpractice.data.PackingCategory
import com.example.travelpractice.data.PackingItem
import com.example.travelpractice.data.PackingRepository
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
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categories.value = repository.getPackingCategories()
                _items.value = repository.getPackingItems()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addCategory(category: PackingCategory) {
        viewModelScope.launch {
            try {
                val categoryId = repository.addPackingCategory(category)
                loadData() // Reload to get updated data
            } catch (e: Exception) {
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
                val itemId = repository.addPackingItem(item)
                loadData()
            } catch (e: Exception) {
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
