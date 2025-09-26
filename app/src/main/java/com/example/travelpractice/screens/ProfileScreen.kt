package com.example.travelpractice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    "Profile",
                    fontWeight = FontWeight.Bold
                ) 
            },
            actions = {
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                }
            }
        )
        
        // User Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = user?.displayName ?: "Traveler",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = user?.email ?: "No email",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // App Statistics
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Your Travel Stats",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = "Trips",
                        value = "0",
                        icon = Icons.Default.Star
                    )
                    StatItem(
                        title = "Tasks",
                        value = "0",
                        icon = Icons.Default.Check
                    )
                    StatItem(
                        title = "Expenses",
                        value = "0",
                        icon = Icons.Default.Star
                    )
                }
            }
        }
        
        // Settings Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                SettingsItem(
                    title = "Notifications",
                    subtitle = "Manage your notifications",
                    icon = Icons.Default.Notifications,
                    onClick = { /* TODO: Implement notifications settings */ }
                )
                
                SettingsItem(
                    title = "Data & Privacy",
                    subtitle = "Manage your data and privacy settings",
                    icon = Icons.Default.Lock,
                    onClick = { /* TODO: Implement privacy settings */ }
                )
                
                SettingsItem(
                    title = "Backup & Sync",
                    subtitle = "Backup your data to the cloud",
                    icon = Icons.Default.Info,
                    onClick = { /* TODO: Implement backup settings */ }
                )
                
                SettingsItem(
                    title = "About",
                    subtitle = "App version and information",
                    icon = Icons.Default.Info,
                    onClick = { /* TODO: Implement about screen */ }
                )
            }
        }
        
        // Quick Actions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        title = "Export Data",
                        icon = Icons.Default.Info,
                        onClick = { /* TODO: Implement data export */ }
                    )
                    
                    ActionButton(
                        title = "Import Data",
                        icon = Icons.Default.Info,
                        onClick = { /* TODO: Implement data import */ }
                    )
                    
                    ActionButton(
                        title = "Clear Data",
                        icon = Icons.Default.Delete,
                        onClick = { /* TODO: Implement clear data */ }
                    )
                }
            }
        }
        
        // Sign Out Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            TextButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Sign Out",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
