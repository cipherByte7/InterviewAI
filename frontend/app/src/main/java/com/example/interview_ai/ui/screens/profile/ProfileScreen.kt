package com.example.interview_ai.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.interview_ai.theme.AccentCyan
import com.example.interview_ai.theme.AppRadius
import com.example.interview_ai.theme.AppSpacing
import com.example.interview_ai.theme.BackgroundDark
import com.example.interview_ai.theme.BorderSubtle
import com.example.interview_ai.theme.Primary
import com.example.interview_ai.theme.PrimaryGlow
import com.example.interview_ai.theme.Success
import com.example.interview_ai.theme.SurfaceDark
import com.example.interview_ai.theme.SurfaceVariantDark
import com.example.interview_ai.theme.TextMuted
import com.example.interview_ai.theme.TextPrimary
import com.example.interview_ai.theme.TextSecondary
import com.example.interview_ai.ui.components.AppTextField
import com.example.interview_ai.ui.components.AppTopBar
import com.example.interview_ai.ui.components.BottomNavBar
import com.example.interview_ai.ui.components.PrimaryButton
import com.example.interview_ai.ui.components.SecondaryButton
import com.example.interview_ai.ui.components.SurfaceCard
import com.example.interview_ai.ui.navigation.Routes
import com.example.interview_ai.viewmodel.AuthViewModel
import com.example.interview_ai.viewmodel.DashboardViewModel
import com.example.interview_ai.viewmodel.ThemeViewModel
import com.example.interview_ai.data.preferences.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    themeViewModel: ThemeViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val selectedTheme by themeViewModel.themeMode.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val BackgroundDark = colorScheme.background
    val SurfaceDark = colorScheme.surface
    val SurfaceVariantDark = colorScheme.surfaceVariant
    val BorderSubtle = colorScheme.outlineVariant
    val TextPrimary = colorScheme.onBackground
    val TextSecondary = colorScheme.onSurfaceVariant
    val TextMuted = colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val PrimaryGlow = colorScheme.primary.copy(alpha = 0.16f)

    var showEditRoleDialog by remember { mutableStateOf(false) }
    var newRoleInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Account Profile",
                subtitle = "Manage your interview credentials",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background
                        ),
                        radius = 1200f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.lg)
            ) {
                // User Credentials Header Card
                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = AppSpacing.lg
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar Orb
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(SurfaceVariantDark, CircleShape)
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(Primary, AccentCyan)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Emblem",
                                tint = Primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(AppSpacing.lg))

                        Column {
                            Text(
                                text = authState.currentUser?.name ?: "Alex Mercer",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = authState.currentUser?.email ?: "alex@example.com",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Stats Dashboard summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                ) {
                    // Avg score badge
                    SurfaceCard(
                        modifier = Modifier.weight(1f),
                        padding = AppSpacing.md
                    ) {
                        Column {
                            Text(
                                text = "Readiness Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${dashboardState.readinessScore}%",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Success
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.xs))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Score Rating",
                                    tint = Success,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Sessions badge
                    SurfaceCard(
                        modifier = Modifier.weight(1f),
                        padding = AppSpacing.md
                    ) {
                        Column {
                            Text(
                                text = "Practice Sessions",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.xs))
                            Text(
                                text = "${dashboardState.totalSessions}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = AppSpacing.md
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "APP THEME",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${selectedTheme.displayName()} selected",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = selectedTheme.icon(),
                                contentDescription = "${selectedTheme.displayName()} theme",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.md))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
                        ) {
                            ThemeOption(ThemeMode.LIGHT, selectedTheme, { themeViewModel.setThemeMode(it) }, Modifier.weight(1f))
                            ThemeOption(ThemeMode.DARK, selectedTheme, { themeViewModel.setThemeMode(it) }, Modifier.weight(1f))
                            ThemeOption(ThemeMode.SYSTEM, selectedTheme, { themeViewModel.setThemeMode(it) }, Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Target Role settings card
                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = AppSpacing.md
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TARGET ENGINEER ROLE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dashboardState.targetRole.ifEmpty { "Android Developer" },
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(Primary.copy(alpha = 0.15f), RoundedCornerShape(AppRadius.md))
                                    .border(1.dp, Primary, RoundedCornerShape(AppRadius.md))
                                    .clickable {
                                        newRoleInput = dashboardState.targetRole
                                        showEditRoleDialog = true
                                    }
                                    .padding(AppSpacing.sm)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Role",
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))

                // Resume skills Tags
                Text(
                    text = "Your Parsed Expertises",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))

                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = AppSpacing.md
                ) {
                    val skills = dashboardState.parsedResume?.skills
                    if (skills.isNullOrEmpty()) {
                        Text(
                            text = "No resume skills parsed yet. Upload your resume from the dashboard to extract keywords.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            skills.chunked(3).forEach { row ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    row.forEach { skill ->
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    SurfaceVariantDark,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    BorderSubtle,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = skill,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.xl))

                // Actions settings list
                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    padding = AppSpacing.sm
                ) {
                    Column {
                        // Clear Local Cache trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    dashboardViewModel.removeResume()
                                }
                                .padding(AppSpacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Clear Cache",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.md))
                            Column {
                                Text(
                                    text = "Reset Resume Status",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Clear uploaded files and extracted tags",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }

                        HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)

                        // Logout trigger
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    println("ProfileScreen: Logout row clicked")
                                    authViewModel.logout {
                                        println("ProfileScreen: Logout onSuccess callback invoked, navigating to login...")
                                        navController.navigate(Routes.Login.route) {
                                            popUpTo(Routes.Dashboard.route) { inclusive = true }
                                        }
                                    }
                                }
                                .padding(AppSpacing.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Log Out Icon",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.md))
                            Column {
                                Text(
                                    text = "Sign Out Session",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Log out from the backend database server",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit target role prompt dialog
    if (showEditRoleDialog) {
        AlertDialog(
            onDismissRequest = { showEditRoleDialog = false },
            title = {
                Text(
                    text = "Update Target Role",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Customize the engineering domain. Generated interview questions will align directly to this role specialization.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    AppTextField(
                        value = newRoleInput,
                        onValueChange = { newRoleInput = it },
                        label = "Engineering Role",
                        placeholder = "e.g. Senior iOS Engineer"
                    )
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "Save",
                    onClick = {
                        if (newRoleInput.trim().isNotEmpty()) {
                            dashboardViewModel.updateTargetRole(newRoleInput.trim())
                            showEditRoleDialog = false
                        }
                    },
                    modifier = Modifier.width(90.dp)
                )
            },
            dismissButton = {
                SecondaryButton(
                    text = "Cancel",
                    onClick = { showEditRoleDialog = false },
                    modifier = Modifier.width(90.dp)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }
}

@Composable
private fun ThemeOption(
    themeMode: ThemeMode,
    selectedTheme: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = themeMode == selectedTheme
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .background(
                if (isSelected) colors.primary.copy(alpha = 0.14f) else colors.surfaceVariant,
                RoundedCornerShape(AppRadius.md)
            )
            .border(
                1.dp,
                if (isSelected) colors.primary else colors.outlineVariant,
                RoundedCornerShape(AppRadius.md)
            )
            .clickable { onSelected(themeMode) }
            .padding(vertical = AppSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = themeMode.icon(),
            contentDescription = themeMode.displayName(),
            tint = if (isSelected) colors.primary else colors.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = themeMode.displayName(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) colors.primary else colors.onSurfaceVariant
        )
    }
}

private fun ThemeMode.displayName(): String = when (this) {
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SYSTEM -> "System"
}

private fun ThemeMode.icon() = when (this) {
    ThemeMode.LIGHT -> Icons.Default.LightMode
    ThemeMode.DARK -> Icons.Default.DarkMode
    ThemeMode.SYSTEM -> Icons.Default.PhoneAndroid
}
