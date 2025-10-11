package com.example.travelpractice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelpractice.data.PackingCategory
import com.example.travelpractice.data.PackingItem

// Predefined categories for smart search
val PREDEFINED_CATEGORIES = listOf(
    "Toiletries", "Clothing", "Travel Essentials", "Electronics", "Documents"
)

// Predefined items for each category
val PREDEFINED_ITEMS = mapOf(
    "Toiletries" to listOf(
        "Toothbrush", "Toothpaste", "Shampoo", "Conditioner", "Soap", "Body Wash",
        "Deodorant", "Razor", "Shaving Cream", "Sunscreen", "Moisturizer", "Face Wash",
        "Hair Brush", "Comb", "Nail Clippers", "Tweezers", "Cotton Swabs", "Tissues"
    ),
    "Clothing" to listOf(
        "Underwear", "Socks", "T-shirts", "Tank Tops", "Pants", "Jeans", "Shorts",
        "Dresses", "Skirts", "Shirts", "Blouses", "Sweaters", "Hoodies", "Jackets",
        "Pajamas", "Sleepwear", "Swimwear", "Belt", "Tie", "Scarf"
    ),
    "Electronics" to listOf(
        "Phone", "Phone Charger", "Laptop", "Laptop Charger", "Tablet", "Headphones",
        "Earbuds", "Camera", "Camera Charger", "Power Bank", "USB Cable", "Adapter",
        "E-reader", "Smartwatch", "Charging Cable", "Bluetooth Speaker", "Portable Charger"
    ),
    "Travel Essentials" to listOf(
        "Passport", "ID Card", "Driver's License", "Tickets", "Boarding Pass", "Wallet",
        "Money", "Credit Cards", "Travel Insurance", "Visa Documents", "Hotel Confirmation",
        "Car Keys", "House Keys", "Travel Pillow", "Eye Mask", "Ear Plugs"
    ),
    "Documents" to listOf(
        "Travel Insurance", "Hotel Reservations", "Flight Tickets", "Train Tickets",
        "Car Rental Confirmation", "Emergency Contacts", "Medical Records", "Prescriptions",
        "Travel Itinerary", "Maps", "Guidebook", "Important Phone Numbers"
    )
)

@Composable
fun SmartCategoryDialog(
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onCreateCustom: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    
    val filteredSuggestions = PREDEFINED_CATEGORIES.filter { 
        it.contains(searchQuery, ignoreCase = true) 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category") },
        text = {
            Column {
                // Search box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        showSuggestions = it.isNotBlank()
                    },
                    label = { Text("Search categories...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                )
                
                // Suggestions list
                if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Suggestions:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.height(150.dp)
                    ) {
                        items(filteredSuggestions) { suggestion ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    onCategorySelected(suggestion)
                                }
                            ) {
                                Text(
                                    text = suggestion,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                
                // Create custom option
                if (searchQuery.isNotBlank() && !filteredSuggestions.contains(searchQuery)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            onCreateCustom(searchQuery)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create custom category: $searchQuery",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CategorySelectionDialog(
    categories: List<PackingCategory>,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category") },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp)
            ) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(android.graphics.Color.parseColor(category.color)).copy(alpha = 0.1f)
                        ),
                        onClick = { onCategorySelected(category.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(android.graphics.Color.parseColor(category.color))
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SmartAddItemDialog(
    category: PackingCategory,
    onDismiss: () -> Unit,
    onAdd: (PackingItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    var addedItems by remember { mutableStateOf<List<PackingItem>>(emptyList()) }
    
    // For predefined categories, show only their items
    // For custom categories, show ALL items from all categories
    val predefinedItems = if (PREDEFINED_ITEMS.containsKey(category.name)) {
        PREDEFINED_ITEMS[category.name] ?: emptyList()
    } else {
        // Custom category - show ALL items from all predefined categories
        PREDEFINED_ITEMS.values.flatten().distinct()
    }
    
    val filteredSuggestions = predefinedItems.filter { 
        it.contains(searchQuery, ignoreCase = true) 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Items to ${category.name}") },
        text = {
            Column {
                // Search box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        showSuggestions = it.isNotBlank()
                    },
                    label = { Text("Search or type item name") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                )
                
                // Suggestions list
                if (showSuggestions && filteredSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Suggestions:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.height(150.dp)
                    ) {
                        items(filteredSuggestions) { suggestion ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    val newItem = PackingItem(
                                        name = suggestion,
                                        categoryId = category.id,
                                        categoryName = category.name,
                                        userId = ""
                                    )
                                    addedItems = addedItems + newItem
                                    searchQuery = ""
                                    showSuggestions = false
                                }
                            ) {
                                Text(
                                    text = suggestion,
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                
                // Create custom item option
                if (searchQuery.isNotBlank() && !filteredSuggestions.contains(searchQuery)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                onClick = {
                            val newItem = PackingItem(
                                name = searchQuery,
                                categoryId = category.id,
                                categoryName = category.name,
                                userId = ""
                            )
                            addedItems = addedItems + newItem
                            searchQuery = ""
                            showSuggestions = false
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Create custom item: $searchQuery",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                // Added items list
                if (addedItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Items to add (${addedItems.size}):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    LazyColumn(
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(addedItems) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 14.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            addedItems = addedItems.filter { it.name != item.name }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.error
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
                    // Add all items at once
                    addedItems.forEach { item ->
                        onAdd(item)
                    }
                    // Close dialog after adding
                    onDismiss()
                },
                enabled = addedItems.isNotEmpty()
            ) {
                Text("Add ${addedItems.size} Items")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
