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
import com.example.travelpractice.data.Expense
import com.example.travelpractice.data.ExpenseCategory
import com.example.travelpractice.data.Budget
import com.example.travelpractice.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackingScreen(
    viewModel: ExpenseViewModel = viewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }
    
    val filteredExpenses = if (selectedCategory != null) {
        expenses.filter { it.category == selectedCategory }
    } else {
        expenses.filter { 
            val expenseDate = Calendar.getInstance().apply { time = it.date }
            val filterDate = Calendar.getInstance().apply { time = selectedDate }
            expenseDate.get(Calendar.DAY_OF_YEAR) == filterDate.get(Calendar.DAY_OF_YEAR) &&
            expenseDate.get(Calendar.YEAR) == filterDate.get(Calendar.YEAR)
        }
    }
    
    val totalSpent = viewModel.getTotalSpent()
    val dailySpent = viewModel.getTotalSpentForDate(selectedDate)
    val remainingBudget = viewModel.getRemainingBudget()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Expense Tracking",
                    fontWeight = FontWeight.Bold
                ) 
            },
            actions = {
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Filter")
                }
                IconButton(onClick = { showBudgetDialog = true }) {
                    Icon(Icons.Default.Star, contentDescription = "Budget")
                }
                IconButton(onClick = { showAddExpenseDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        )
        
        // Budget Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (remainingBudget < 0) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Budget Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = if (remainingBudget < 0) "Over Budget!" else "On Track",
                        color = if (remainingBudget < 0) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Spent",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format("%.2f", totalSpent)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Budget",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format("%.2f", budget?.totalBudget ?: 0.0)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Remaining",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format("%.2f", remainingBudget)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingBudget < 0) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }
            }
        }
        
        // Daily Spending
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        text = "Today's Spending",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", dailySpent)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                TextButton(
                    onClick = { /* TODO: Show date picker */ }
                ) {
                    Text(
                        text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(selectedDate)
                    )
                }
            }
        }
        
        // Category Breakdown
        if (expenses.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Spending by Category",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExpenseCategory.entries.forEach { category ->
                        val categoryTotal = viewModel.getTotalSpentByCategory(category)
                        if (categoryTotal > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${category.icon} ${category.displayName}",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$${String.format("%.2f", categoryTotal)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
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
            // Expenses List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredExpenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No expenses found",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredExpenses) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onEdit = { /* TODO: Implement edit */ },
                            onDelete = { viewModel.deleteExpense(expense.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onAdd = { expense ->
                viewModel.addExpense(expense)
                showAddExpenseDialog = false
            }
        )
    }
    
    // Budget Dialog
    if (showBudgetDialog) {
        BudgetDialog(
            currentBudget = budget,
            onDismiss = { showBudgetDialog = false },
            onSave = { budget ->
                viewModel.saveBudget(budget)
                showBudgetDialog = false
            }
        )
    }
    
    // Filter Dialog
    if (showFilterDialog) {
        ExpenseFilterDialog(
            currentCategory = selectedCategory,
            selectedDate = selectedDate,
            onDismiss = { showFilterDialog = false },
            onCategoryChange = { selectedCategory = it },
            onDateChange = { selectedDate = it }
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
fun ExpenseCard(
    expense: Expense,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    
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
            // Category icon
            Text(
                text = expense.category.icon,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = expense.category.displayName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = dateFormat.format(expense.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "$${String.format("%.2f", expense.amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
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
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAdd: (Expense) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.OTHER) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("$") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category:")
                ExpenseCategory.entries.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                        Text(
                            text = "${category.icon} ${category.displayName}",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > 0 && description.isNotBlank()) {
                        onAdd(
                            Expense(
                                amount = amountValue,
                                description = description,
                                category = selectedCategory
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

@Composable
fun BudgetDialog(
    currentBudget: Budget?,
    onDismiss: () -> Unit,
    onSave: (Budget) -> Unit
) {
    var totalBudget by remember { mutableStateOf(currentBudget?.totalBudget?.toString() ?: "") }
    var dailyBudget by remember { mutableStateOf(currentBudget?.dailyBudget?.toString() ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Budget") },
        text = {
            Column {
                OutlinedTextField(
                    value = totalBudget,
                    onValueChange = { totalBudget = it },
                    label = { Text("Total Budget") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("$") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = dailyBudget,
                    onValueChange = { dailyBudget = it },
                    label = { Text("Daily Budget (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Text("$") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val total = totalBudget.toDoubleOrNull()
                    val daily = dailyBudget.toDoubleOrNull()
                    if (total != null && total > 0) {
                        onSave(
                            Budget(
                                totalBudget = total,
                                dailyBudget = daily ?: 0.0
                            )
                        )
                    }
                }
            ) {
                Text("Save")
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
fun ExpenseFilterDialog(
    currentCategory: ExpenseCategory?,
    selectedDate: Date,
    onDismiss: () -> Unit,
    onCategoryChange: (ExpenseCategory?) -> Unit,
    onDateChange: (Date) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Expenses") },
        text = {
            Column {
                Text("Category:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentCategory == null,
                        onClick = { onCategoryChange(null) }
                    )
                    Text(
                        text = "All Categories",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                ExpenseCategory.entries.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentCategory == category,
                            onClick = { onCategoryChange(category) }
                        )
                        Text(
                            text = "${category.icon} ${category.displayName}",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Date:")
                TextButton(
                    onClick = { /* TODO: Show date picker */ }
                ) {
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Apply")
            }
        }
    )
}
