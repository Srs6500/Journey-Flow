package com.example.travelpractice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
    
    val predefinedItems = PREDEFINED_ITEMS[category.name] ?: emptyList()
    val filteredSuggestions = predefinedItems.filter { 
        it.contains(searchQuery, ignoreCase = true) 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item to ${category.name}") },
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
                                    searchQuery = suggestion
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        onAdd(
                            PackingItem(
                                name = searchQuery,
                                categoryId = category.id,
                                categoryName = category.name,
                                userId = ""
                            )
                        )
                    }
                }
            ) {
                Text("Add Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
