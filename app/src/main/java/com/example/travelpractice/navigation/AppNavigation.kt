package com.example.travelpractice.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.travelpractice.screens.PackingListScreen
import com.example.travelpractice.screens.EnhancedChecklistScreen
import com.example.travelpractice.screens.ReviewsScreen
// import com.example.travelpractice.screens.TravelTasksScreen // REMOVED
// import com.example.travelpractice.screens.ExpenseTrackingScreen // REMOVED
// import com.example.travelpractice.screens.ProfileScreen // REMOVED
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Packing : Screen("packing", "Packing", Icons.Default.List)
    object Reviews : Screen("reviews", "Reviews", Icons.Default.Star)
    // object Tasks : Screen("tasks", "Tasks", Icons.Default.Check) // REMOVED
    // object Expenses : Screen("expenses", "Expenses", Icons.Default.Star) // REMOVED
    // object Profile : Screen("profile", "Profile", Icons.Default.Person) // REMOVED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    
    val screens = listOf(
        Screen.Packing,
        Screen.Reviews
        // Screen.Tasks, // REMOVED
        // Screen.Expenses, // REMOVED
        // Screen.Profile // REMOVED
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Packing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Packing.route) {
                EnhancedChecklistScreen(
                    onSignOut = {
                        auth.signOut()
                    }
                )
            }
            composable(Screen.Reviews.route) {
                ReviewsScreen(
                    onSignOut = {
                        auth.signOut()
                    }
                )
            }
            // composable(Screen.Tasks.route) { // REMOVED
            //     TravelTasksScreen()
            // }
            // composable(Screen.Expenses.route) { // REMOVED
            //     ExpenseTrackingScreen()
            // }
            // composable(Screen.Profile.route) { // REMOVED
            //     ProfileScreen(
            //         onSignOut = {
            //             auth.signOut()
            //         }
            //     )
            // }
        }
    }
}
