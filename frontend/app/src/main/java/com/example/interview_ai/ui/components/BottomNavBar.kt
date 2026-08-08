package com.example.interview_ai.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.interview_ai.theme.*
import com.example.interview_ai.ui.navigation.Routes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        containerColor = colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(topStart = AppRadius.lg, topEnd = AppRadius.lg))
    ) {
        // Home
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Routes.Dashboard.route,
            onClick = {
                if (currentRoute != Routes.Dashboard.route) {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                selectedTextColor = colorScheme.primary,
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                indicatorColor = colorScheme.primary.copy(alpha = 0.12f)
            )
        )
        // History
        NavigationBarItem(
            icon = { Icon(Icons.Default.Refresh, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == Routes.History.route,
            onClick = {
                if (currentRoute != Routes.History.route) {
                    navController.navigate(Routes.History.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                selectedTextColor = colorScheme.primary,
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                indicatorColor = colorScheme.primary.copy(alpha = 0.12f)
            )
        )
        // Profile
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == Routes.Profile.route,
            onClick = {
                if (currentRoute != Routes.Profile.route) {
                    navController.navigate(Routes.Profile.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                selectedTextColor = colorScheme.primary,
                unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                indicatorColor = colorScheme.primary.copy(alpha = 0.12f)
            )
        )
    }
}
