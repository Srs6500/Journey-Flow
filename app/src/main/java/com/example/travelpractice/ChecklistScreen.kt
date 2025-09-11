package com.example.travelpractice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChecklistItem(
    val id: String,
    val name: String,
    val category: String,
    var isChecked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(onSignOut: () -> Unit = {}) {
    var showRemainingItems by remember { mutableStateOf(false) }
    
    // Default packing list organized by categories
    val defaultItems = remember {
        listOf(
            // Toiletries
            ChecklistItem("1", "Toothbrush", "Toiletries"),
            ChecklistItem("2", "Toothpaste", "Toiletries"),
            ChecklistItem("3", "Shampoo", "Toiletries"),
            ChecklistItem("4", "Soap", "Toiletries"),
            ChecklistItem("5", "Deodorant", "Toiletries"),
            ChecklistItem("6", "Razor", "Toiletries"),
            ChecklistItem("7", "Sunscreen", "Toiletries"),
            
            // Clothing
            ChecklistItem("8", "Underwear", "Clothing"),
            ChecklistItem("9", "Socks", "Clothing"),
            ChecklistItem("10", "T-shirts", "Clothing"),
            ChecklistItem("11", "Pants/Jeans", "Clothing"),
            ChecklistItem("12", "Shorts", "Clothing"),
            ChecklistItem("13", "Dress/Shirt", "Clothing"),
            ChecklistItem("14", "Sweater/Jacket", "Clothing"),
            ChecklistItem("15", "Pajamas", "Clothing"),
            
            // Travel Essentials
            ChecklistItem("16", "Passport/ID", "Travel Essentials"),
            ChecklistItem("17", "Tickets/Boarding Pass", "Travel Essentials"),
            ChecklistItem("18", "Wallet", "Travel Essentials"),
            ChecklistItem("19", "Phone Charger", "Travel Essentials"),
            ChecklistItem("20", "Headphones", "Travel Essentials"),
            ChecklistItem("21", "Camera", "Travel Essentials"),
            ChecklistItem("22", "Medications", "Travel Essentials"),
            ChecklistItem("23", "First Aid Kit", "Travel Essentials"),
            ChecklistItem("24", "Travel Adapter", "Travel Essentials"),
            ChecklistItem("25", "Luggage Tags", "Travel Essentials")
        )
    }
    
    var items by remember { mutableStateOf(defaultItems) }
    
    val categories = items.groupBy { it.category }
    val remainingItems = items.filter { !it.isChecked }
    
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
            navigationIcon = {
                IconButton(onClick = { /* TODO: Navigate back to login */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                TextButton(
                    onClick = { showRemainingItems = !showRemainingItems }
                ) {
                    Text("Remaining: ${remainingItems.size}")
                }
                TextButton(
                    onClick = { 
                        onSignOut()
                    }
                ) {
                    Text("Sign Out")
                }
            }
        )
        
        if (showRemainingItems) {
            // Show only remaining items
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Remaining Items (${remainingItems.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                items(remainingItems) { item ->
                    ChecklistItemRow(
                        item = item,
                        onCheckedChange = { isChecked ->
                            items = items.map { 
                                if (it.id == item.id) it.copy(isChecked = isChecked) 
                                else it 
                            }
                        }
                    )
                }
            }
        } else {
            // Show all items by category
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categories.forEach { (category, categoryItems) ->
                    item {
                        Text(
                            text = category,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(categoryItems) { item ->
                        ChecklistItemRow(
                            item = item,
                            onCheckedChange = { isChecked ->
                                items = items.map { 
                                    if (it.id == item.id) it.copy(isChecked = isChecked) 
                                    else it 
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit
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
            
            Text(
                text = item.name,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
                style = if (item.isChecked) {
                    MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    MaterialTheme.typography.bodyMedium
                }
            )
            
            if (item.isChecked) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
