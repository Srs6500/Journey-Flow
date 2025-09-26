package com.example.travelpractice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelpractice.data.PackingCategory
import com.example.travelpractice.data.PackingItem
import com.example.travelpractice.viewmodel.PackingViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackingListScreen(
    onSignOut: () -> Unit,
    viewModel: PackingViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf("") }
    var showRemainingOnly by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Create default categories if none exist
    LaunchedEffect(categories) {
        if (categories.isEmpty()) {
            val defaultCategories = listOf(
                PackingCategory(name = "Toiletries", color = "#FF6200EE", isDefault = true),
                PackingCategory(name = "Clothing", color = "#FF03DAC5", isDefault = true),
                PackingCategory(name = "Travel Essentials", color = "#FF6200EE", isDefault = true),
                PackingCategory(name = "Electronics", color = "#FF03DAC5", isDefault = true),
                PackingCategory(name = "Documents", color = "#FF6200EE", isDefault = true)
            )
            defaultCategories.forEach { category ->
                viewModel.addCategory(category)
            }
        }
    }
    
    val filteredItems = if (showRemainingOnly) {
        viewModel.getRemainingItems().filter { 
            it.name.contains(searchQuery, ignoreCase = true) 
        }
    } else {
        items.filter { 
            it.name.contains(searchQuery, ignoreCase = true) 
        }
    }
    
    val itemsByCategory = filteredItems.groupBy { it.categoryId }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Packing Checklist",
                    fontWeight = FontWeight.Bold
                ) 
            },
            actions = {
                IconButton(onClick = { showAddCategoryDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Category")
                }
                IconButton(onClick = { showRemainingOnly = !showRemainingOnly }) {
                    Icon(
                        if (showRemainingOnly) Icons.Default.List else Icons.Default.Search,
                        contentDescription = "Toggle View"
                    )
                }
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        )
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search items...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        
        // Progress Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress: ${items.count { it.isChecked }}/${items.size}",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${((items.count { it.isChecked }.toFloat() / items.size.coerceAtLeast(1)) * 100).toInt()}%",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Items List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showRemainingOnly) {
                    item {
                        Text(
                            text = "Remaining Items (${filteredItems.size})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(filteredItems) { item ->
                        val category = categories.find { it.id == item.categoryId }
                        PackingItemCard(
                            item = item,
                            category = category,
                            onCheckedChange = { isChecked ->
                                viewModel.updateItem(item.copy(isChecked = isChecked))
                            },
                            onEdit = { /* TODO: Implement edit */ },
                            onDelete = { viewModel.deleteItem(item.id) }
                        )
                    }
                } else {
                    categories.forEach { category ->
                        val categoryItems = itemsByCategory[category.id] ?: emptyList()
                        if (categoryItems.isNotEmpty()) {
                            item {
                                CategoryHeader(
                                    category = category,
                                    itemCount = categoryItems.size,
                                    checkedCount = categoryItems.count { it.isChecked },
                                    onAddItem = { 
                                        selectedCategoryId = category.id
                                        showAddItemDialog = true 
                                    },
                                    onEditCategory = { /* TODO: Implement edit category */ },
                                    onDeleteCategory = { viewModel.deleteCategory(category.id) }
                                )
                            }
                            
                            items(categoryItems) { item ->
                                PackingItemCard(
                                    item = item,
                                    category = category,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateItem(item.copy(isChecked = isChecked))
                                    },
                                    onEdit = { /* TODO: Implement edit */ },
                                    onDelete = { viewModel.deleteItem(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Add Category Dialog
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onAdd = { category ->
                viewModel.addCategory(category)
                showAddCategoryDialog = false
            }
        )
    }
    
    // Add Item Dialog
    if (showAddItemDialog) {
        AddItemDialog(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onDismiss = { showAddItemDialog = false },
            onAdd = { item ->
                viewModel.addItem(item)
                showAddItemDialog = false
            }
        )
    }
    
    // Error Message
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snackbar or toast
        }
        viewModel.clearError()
    }
}

@Composable
fun CategoryHeader(
    category: PackingCategory,
    itemCount: Int,
    checkedCount: Int,
    onAddItem: () -> Unit,
    onEditCategory: () -> Unit,
    onDeleteCategory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(category.color)).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(android.graphics.Color.parseColor(category.color))
                )
                Text(
                    text = "$checkedCount/$itemCount items",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                IconButton(onClick = onAddItem) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
                IconButton(onClick = onEditCategory) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Category")
                }
                IconButton(onClick = onDeleteCategory) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Category")
                }
            }
        }
    }
}

@Composable
fun PackingItemCard(
    item: PackingItem,
    category: PackingCategory?,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    style = if (item.isChecked) {
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        MaterialTheme.typography.bodyMedium
                    }
                )
                category?.let {
                    Text(
                        text = it.name,
                        fontSize = 12.sp,
                        color = Color(android.graphics.Color.parseColor(it.color))
                    )
                }
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (PackingCategory) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF6200EE") }
    
    val colors = listOf(
        "#FF6200EE", "#FF03DAC5", "#FF6200EE", "#FF03DAC5", "#FF6200EE"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Choose Color:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(android.graphics.Color.parseColor(color))
                                ),
                                onClick = { selectedColor = color }
                            ) {
                                if (selectedColor == color) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(PackingCategory(name = name, color = selectedColor))
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddItemDialog(
    categories: List<PackingCategory>,
    selectedCategoryId: String,
    onDismiss: () -> Unit,
    onAdd: (PackingItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(selectedCategoryId) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category:")
                categories.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category.id,
                            onClick = { selectedCategory = category.id }
                        )
                        Text(
                            text = category.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && selectedCategory.isNotBlank()) {
                        onAdd(PackingItem(name = name, categoryId = selectedCategory))
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
