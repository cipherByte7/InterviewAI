package com.example.interview_ai.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(topStart = AppRadius.lg, topEnd = AppRadius.lg))
    ) {
        // Home
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Routes.Dashboard.route,
            onClick = {
                navController.navigate(Routes.Dashboard.route) {
                    popUpTo(Routes.Dashboard.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                unselectedIconColor = TextMuted,
                selectedTextColor = TextPrimary,
                unselectedTextColor = TextMuted,
                indicatorColor = Primary
            )
        )
        // History
        NavigationBarItem(
            icon = { Icon(Icons.Default.Refresh, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == Routes.History.route,
            onClick = {
                navController.navigate(Routes.History.route) {
                    popUpTo(Routes.History.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                unselectedIconColor = TextMuted,
                selectedTextColor = TextPrimary,
                unselectedTextColor = TextMuted,
                indicatorColor = Primary
            )
        )
        // Profile
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == Routes.Profile.route,
            onClick = {
                navController.navigate(Routes.Profile.route) {
                    popUpTo(Routes.Profile.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                unselectedIconColor = TextMuted,
                selectedTextColor = TextPrimary,
                unselectedTextColor = TextMuted,
                indicatorColor = Primary
            )
        )
    }
}
