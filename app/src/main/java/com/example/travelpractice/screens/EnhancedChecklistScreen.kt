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
fun EnhancedChecklistScreen(
    onSignOut: () -> Unit,
    viewModel: PackingViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<PackingCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    // Categories will be created manually via SmartCategoryDialog
    
    // Calculate statistics
    val totalItems = items.size
    val checkedItems = items.count { it.isChecked }
    val progressPercentage = if (totalItems > 0) (checkedItems.toFloat() / totalItems * 100).toInt() else 0
    
    // Filter categories based on search
    val filteredCategories = categories.filter { 
        it.name.contains(searchQuery, ignoreCase = true) 
    }
    
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
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        )
        
        // Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Packing Progress",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$progressPercentage%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = progressPercentage / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$checkedItems of $totalItems items packed",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search categories...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Debug information
            LaunchedEffect(categories) {
                println("DEBUG: Categories count: ${categories.size}")
                categories.forEach { category ->
                    println("DEBUG: Category: ${category.name}, ID: ${category.id}")
                }
            }
            
            // Categories List with Items
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredCategories.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.List,
                                        contentDescription = "No Categories",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (searchQuery.isNotEmpty()) "No categories found" else "No categories yet",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Tap + to add your first category",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(filteredCategories) { category ->
                        val categoryItems = items.filter { it.categoryId == category.id }
                        val isExpanded = expandedCategories.contains(category.id)
                        
                        CategoryCard(
                            category = category,
                            items = categoryItems,
                            isExpanded = isExpanded,
                            onToggleExpanded = { 
                                expandedCategories = if (isExpanded) {
                                    expandedCategories - category.id
                                } else {
                                    expandedCategories + category.id
                                }
                            },
                            onAddItem = { 
                                selectedCategory = category
                                showAddItemDialog = true 
                            },
                            onUpdateItem = { item ->
                                viewModel.updateItem(item)
                            },
                            onDeleteItem = { itemId ->
                                viewModel.deleteItem(itemId)
                            },
                            onDeleteCategory = { categoryId ->
                                viewModel.deleteCategory(categoryId)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Smart Category Dialog
    if (showAddCategoryDialog) {
        SmartCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onCategorySelected = { categoryName ->
                // Find existing category or create new one
                val existingCategory = categories.find { it.name == categoryName }
                if (existingCategory != null) {
                    selectedCategory = existingCategory
                    showAddCategoryDialog = false
                    showAddItemDialog = true
                } else {
                    // Create new predefined category and immediately open item dialog
                    val newCategory = PackingCategory(
                        name = categoryName,
                        color = when (categoryName) {
                            "Toiletries" -> "#FF6200EE"
                            "Clothing" -> "#FF03DAC5"
                            "Travel Essentials" -> "#FF4CAF50"
                            "Electronics" -> "#FFFF9800"
                            "Documents" -> "#FFF44336"
                            else -> "#FF6200EE"
                        },
                        default = true
                    )
                    viewModel.addCategory(newCategory)
                    selectedCategory = newCategory
                    showAddCategoryDialog = false
                    showAddItemDialog = true
                }
            },
            onCreateCustom = { categoryName ->
                // Create custom category and immediately open item dialog
                val customCategory = PackingCategory(
                    name = categoryName,
                    color = "#FF6200EE", // Default color
                    default = false
                )
                viewModel.addCategory(customCategory)
                selectedCategory = customCategory
                showAddCategoryDialog = false
                showAddItemDialog = true
            }
        )
    }
    
    // Add Item Dialog
    if (showAddItemDialog && selectedCategory != null) {
        SmartAddItemDialog(
            category = selectedCategory!!,
            onDismiss = { 
                showAddItemDialog = false
                selectedCategory = null
            },
            onAdd = { item ->
                viewModel.addItem(item)
                // Force refresh after adding item
                viewModel.loadData()
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
fun CategoryCard(
    category: PackingCategory,
    items: List<PackingItem>,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onAddItem: () -> Unit,
    onUpdateItem: (PackingItem) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDeleteCategory: (String) -> Unit
) {
    val checkedCount = items.count { it.isChecked }
    val totalCount = items.size
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(category.color)).copy(alpha = 0.1f)
        )
    ) {
        Column {
            // Category Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onToggleExpanded) {
                        Icon(
                            if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                    
                    Column {
                        Text(
                            text = category.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(android.graphics.Color.parseColor(category.color))
                        )
                        Text(
                            text = "$checkedCount/$totalCount items",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onAddItem) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                    IconButton(onClick = { onDeleteCategory(category.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Category")
                    }
                }
            }
            
            // Items List (when expanded)
            if (isExpanded) {
                if (items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items yet. Tap + to add items.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items.forEach { item ->
                        PackingItemCard(
                            item = item,
                            category = category,
                            onCheckedChange = { isChecked: Boolean ->
                                onUpdateItem(item.copy(isChecked = isChecked))
                            },
                            onEdit = { },
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }
}

// Removed unused view functions - focusing on the new CategoryCard approach

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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
fun EnhancedAddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (PackingCategory) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF6200EE") }
    
    val colors = listOf(
        "#FF6200EE", "#FF03DAC5", "#FF4CAF50", "#FFFF9800", "#FFF44336"
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
                        onAdd(PackingCategory(name = name, color = selectedColor, userId = ""))
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
fun EnhancedAddItemDialog(
    category: PackingCategory,
    onDismiss: () -> Unit,
    onAdd: (PackingItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item to ${category.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., Toothbrush, Shampoo, etc.") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            PackingItem(
                                name = name,
                                categoryId = category.id,
                                categoryName = category.name,
                                userId = ""
                            )
                        )
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
