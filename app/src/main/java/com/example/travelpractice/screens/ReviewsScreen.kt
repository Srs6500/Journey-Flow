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
import com.example.travelpractice.data.Review
import com.example.travelpractice.viewmodel.ReviewsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

// Predefined popular travel destinations
val POPULAR_DESTINATIONS = listOf(
    "Paris, France", "London, UK", "New York, USA", "Tokyo, Japan", "Rome, Italy",
    "Barcelona, Spain", "Amsterdam, Netherlands", "Prague, Czech Republic", "Vienna, Austria",
    "Berlin, Germany", "Madrid, Spain", "Florence, Italy", "Venice, Italy", "Athens, Greece",
    "Istanbul, Turkey", "Dubai, UAE", "Singapore", "Hong Kong", "Bangkok, Thailand",
    "Sydney, Australia", "Melbourne, Australia", "Los Angeles, USA", "San Francisco, USA",
    "Miami, USA", "Las Vegas, USA", "Chicago, USA", "Boston, USA", "Seattle, USA",
    "Toronto, Canada", "Vancouver, Canada", "Montreal, Canada", "Mexico City, Mexico",
    "Cancun, Mexico", "Rio de Janeiro, Brazil", "Buenos Aires, Argentina", "Lima, Peru",
    "Cape Town, South Africa", "Marrakech, Morocco", "Cairo, Egypt", "Mumbai, India",
    "Delhi, India", "Goa, India", "Kathmandu, Nepal", "Kathmandu, Nepal", "Bali, Indonesia",
    "Phuket, Thailand", "Seoul, South Korea", "Beijing, China", "Shanghai, China",
    "Moscow, Russia", "St. Petersburg, Russia", "Stockholm, Sweden", "Oslo, Norway",
    "Copenhagen, Denmark", "Helsinki, Finland", "Reykjavik, Iceland", "Dublin, Ireland",
    "Edinburgh, Scotland", "Zurich, Switzerland", "Geneva, Switzerland", "Brussels, Belgium",
    "Lisbon, Portugal", "Porto, Portugal", "Warsaw, Poland", "Krakow, Poland",
    "Budapest, Hungary", "Bucharest, Romania", "Sofia, Bulgaria", "Zagreb, Croatia",
    "Split, Croatia", "Santorini, Greece", "Mykonos, Greece", "Santorini, Greece",
    "Santorini, Greece", "Santorini, Greece", "Santorini, Greece", "Santorini, Greece"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    onSignOut: () -> Unit,
    viewModel: ReviewsViewModel = viewModel()
) {
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var showEditReviewDialog by remember { mutableStateOf(false) }
    var editingReview by remember { mutableStateOf<Review?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var sortBy by remember { mutableStateOf("date") } // "date" or "rating"
    
    // Load reviews when screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadReviews()
    }
    
    // Filter reviews based on search
    val filteredReviews = reviews.filter { 
        it.destination.contains(searchQuery, ignoreCase = true) ||
        it.comment.contains(searchQuery, ignoreCase = true)
    }
    
    // Sort reviews
    val sortedReviews = when (sortBy) {
        "rating" -> filteredReviews.sortedByDescending { it.rating }
        else -> filteredReviews.sortedByDescending { it.createdAt }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Travel Reviews",
                    fontWeight = FontWeight.Bold
                ) 
            },
            actions = {
                IconButton(onClick = { showAddReviewDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Review")
                }
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        )
        
        // Search and Sort Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search reviews...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            // Sort Dropdown
            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Sort")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort by Date") },
                        onClick = {
                            sortBy = "date"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Rating") },
                        onClick = {
                            sortBy = "rating"
                            expanded = false
                        }
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
            
            // Reviews List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (sortedReviews.isEmpty()) {
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
                                        Icons.Default.Star,
                                        contentDescription = "No Reviews",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (searchQuery.isNotEmpty()) "No reviews found" else "No reviews yet",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Tap + to add your first review",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    items(sortedReviews) { review ->
                        ReviewCard(
                            review = review,
                            onEdit = { 
                                editingReview = review
                                showEditReviewDialog = true
                            },
                            onDelete = { viewModel.deleteReview(review.id) }
                        )
                    }
                }
            }
        }
    }
    
    // Add Review Dialog
    if (showAddReviewDialog) {
        AddReviewDialog(
            onDismiss = { showAddReviewDialog = false },
            onAdd = { review ->
                viewModel.addReview(review)
                showAddReviewDialog = false
            }
        )
    }
    
    // Edit Review Dialog
    if (showEditReviewDialog && editingReview != null) {
        EditReviewDialog(
            review = editingReview!!,
            onDismiss = { 
                showEditReviewDialog = false
                editingReview = null
            },
            onUpdate = { updatedReview ->
                viewModel.updateReview(updatedReview)
                showEditReviewDialog = false
                editingReview = null
            }
        )
    }
    
    // Error Message
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            println("DEBUG: Error message: $message")
        }
        viewModel.clearError()
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with username and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.username,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(review.createdAt)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Destination
            Text(
                text = review.destination,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Star Rating
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        if (index < review.rating) Icons.Default.Star else Icons.Default.Star,
                        contentDescription = "Star ${index + 1}",
                        tint = if (index < review.rating) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${review.rating}/5",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Comment
            Text(
                text = review.comment,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun EditReviewDialog(
    review: Review,
    onDismiss: () -> Unit,
    onUpdate: (Review) -> Unit
) {
    var destination by remember { mutableStateOf(review.destination) }
    var rating by remember { mutableStateOf(review.rating) }
    var comment by remember { mutableStateOf(review.comment) }
    var showSuggestions by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val username = currentUser?.email?.substringBefore("@") ?: "Anonymous"

    val filteredDestinations = POPULAR_DESTINATIONS.filter {
        it.contains(destination, ignoreCase = true)
    }.take(5)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Review") },
        text = {
            Column {
                OutlinedTextField(
                    value = destination,
                    onValueChange = {
                        destination = it
                        showSuggestions = it.isNotBlank()
                    },
                    label = { Text("Destination") },
                    placeholder = { Text("e.g., Paris, France") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (showSuggestions && filteredDestinations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Popular destinations:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(filteredDestinations) { suggestion ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    destination = suggestion
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

                Spacer(modifier = Modifier.height(16.dp))

                Text("Rating:")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        IconButton(onClick = { rating = index + 1 }) {
                            Icon(
                                if (index < rating) Icons.Default.Star else Icons.Default.Star,
                                contentDescription = "Star ${index + 1}",
                                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rating > 0) "$rating/5" else "Tap to rate",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your Review") },
                    placeholder = { Text("Share your experience...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (destination.isNotBlank() && rating > 0 && comment.isNotBlank()) {
                        onUpdate(
                            review.copy(
                                destination = destination,
                                rating = rating,
                                comment = comment
                            )
                        )
                    }
                }
            ) {
                Text("Update Review")
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
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onAdd: (Review) -> Unit
) {
    var destination by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val username = currentUser?.email?.substringBefore("@") ?: "Anonymous"
    
    // Filter destinations based on search
    val filteredDestinations = POPULAR_DESTINATIONS.filter { 
        it.contains(destination, ignoreCase = true) 
    }.take(5) // Show top 5 suggestions
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Review") },
        text = {
            Column {
                // Destination Field with Search
                OutlinedTextField(
                    value = destination,
                    onValueChange = { 
                        destination = it
                        showSuggestions = it.isNotBlank()
                    },
                    label = { Text("Destination") },
                    placeholder = { Text("e.g., Paris, France") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Destination Suggestions
                if (showSuggestions && filteredDestinations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Popular destinations:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyColumn(
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(filteredDestinations) { suggestion ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    destination = suggestion
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Star Rating
                Text("Rating:")
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 }
                        ) {
                            Icon(
                                if (index < rating) Icons.Default.Star else Icons.Default.Star,
                                contentDescription = "Star ${index + 1}",
                                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rating > 0) "$rating/5" else "Tap to rate",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Comment Field
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Your Review") },
                    placeholder = { Text("Share your experience...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
                        TextButton(
                            onClick = {
                                if (destination.isNotBlank() && rating > 0 && comment.isNotBlank()) {
                                    onAdd(
                                        Review(
                                            userId = currentUser?.uid ?: "",
                                            username = username,
                                            destination = destination,
                                            rating = rating,
                                            comment = comment
                                        )
                                    )
                                }
                            }
                        ) {
                            Text("Add Review")
                        }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
