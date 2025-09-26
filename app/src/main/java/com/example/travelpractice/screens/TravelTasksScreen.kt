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
import com.example.travelpractice.data.TravelTask
import com.example.travelpractice.data.TaskCategory
import com.example.travelpractice.data.TaskPriority
import com.example.travelpractice.viewmodel.TasksViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelTasksScreen(
    viewModel: TasksViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(TaskCategory.OTHER) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf<TaskCategory?>(null) }
    var showCompleted by remember { mutableStateOf(false) }
    
    val filteredTasks = if (filterCategory != null) {
        tasks.filter { it.category == filterCategory }
    } else if (showCompleted) {
        tasks.filter { it.isCompleted }
    } else {
        tasks.filter { !it.isCompleted }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Travel Tasks",
                    fontWeight = FontWeight.Bold
                ) 
            },
            actions = {
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Filter")
                }
                IconButton(onClick = { showAddTaskDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        )
        
        // Task Summary Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskSummaryCard(
                title = "Total",
                count = tasks.size,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            TaskSummaryCard(
                title = "Pending",
                count = tasks.count { !it.isCompleted },
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            TaskSummaryCard(
                title = "Completed",
                count = tasks.count { it.isCompleted },
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Overdue Tasks Alert
        val overdueTasks = viewModel.getOverdueTasks()
        if (overdueTasks.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${overdueTasks.size} overdue task(s)",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
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
            // Tasks List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tasks found",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onEdit = { /* TODO: Implement edit */ },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAdd = { task ->
                viewModel.addTask(task)
                showAddTaskDialog = false
            }
        )
    }
    
    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            currentCategory = filterCategory,
            showCompleted = showCompleted,
            onDismiss = { showFilterDialog = false },
            onCategoryChange = { filterCategory = it },
            onShowCompletedChange = { showCompleted = it }
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
fun TaskSummaryCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TravelTask,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val isOverdue = !task.isCompleted && task.dueDate?.before(Date()) == true
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() },
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    style = if (task.isCompleted) {
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        MaterialTheme.typography.bodyMedium
                    }
                )
                
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category chip
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = getCategoryColor(task.category).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = task.category.displayName,
                            fontSize = 10.sp,
                            color = getCategoryColor(task.category),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Priority chip
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = getPriorityColor(task.priority).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = task.priority.displayName,
                            fontSize = 10.sp,
                            color = getPriorityColor(task.priority),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Due date
                    task.dueDate?.let { dueDate ->
                        Text(
                            text = "Due: ${dateFormat.format(dueDate)}",
                            fontSize = 12.sp,
                            color = if (isOverdue) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
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
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (TravelTask) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.OTHER) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Category:")
                TaskCategory.entries.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                        Text(
                            text = category.displayName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Priority:")
                TaskPriority.entries.forEach { priority ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority }
                        )
                        Text(
                            text = priority.displayName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Due Date:")
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(
                            text = dueDate?.let { 
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                            } ?: "Select Date"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            TravelTask(
                                title = title,
                                description = description,
                                category = selectedCategory,
                                priority = selectedPriority,
                                dueDate = dueDate
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
fun FilterDialog(
    currentCategory: TaskCategory?,
    showCompleted: Boolean,
    onDismiss: () -> Unit,
    onCategoryChange: (TaskCategory?) -> Unit,
    onShowCompletedChange: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Tasks") },
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
                
                TaskCategory.entries.forEach { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentCategory == category,
                            onClick = { onCategoryChange(category) }
                        )
                        Text(
                            text = category.displayName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showCompleted,
                        onCheckedChange = onShowCompletedChange
                    )
                    Text(
                        text = "Show completed tasks",
                        modifier = Modifier.padding(start = 8.dp)
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

fun getCategoryColor(category: TaskCategory): Color {
    return when (category) {
        TaskCategory.PASSPORT -> Color(0xFFE91E63)
        TaskCategory.TRANSPORTATION -> Color(0xFF2196F3)
        TaskCategory.ACCOMMODATION -> Color(0xFF4CAF50)
        TaskCategory.INSURANCE -> Color(0xFFFF9800)
        TaskCategory.HEALTH -> Color(0xFFF44336)
        TaskCategory.MONEY -> Color(0xFF9C27B0)
        TaskCategory.COMMUNICATION -> Color(0xFF00BCD4)
        TaskCategory.PACKING -> Color(0xFF795548)
        TaskCategory.OTHER -> Color(0xFF607D8B)
    }
}

fun getPriorityColor(priority: TaskPriority): Color {
    return when (priority) {
        TaskPriority.LOW -> Color(0xFF4CAF50)
        TaskPriority.MEDIUM -> Color(0xFFFF9800)
        TaskPriority.HIGH -> Color(0xFFFF5722)
        TaskPriority.URGENT -> Color(0xFFF44336)
    }
}
