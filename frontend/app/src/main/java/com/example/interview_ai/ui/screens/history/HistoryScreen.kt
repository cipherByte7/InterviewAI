package com.example.interview_ai.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.example.interview_ai.ui.components.BottomNavBar
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.interview_ai.theme.AppRadius
import com.example.interview_ai.theme.AppSpacing
import com.example.interview_ai.theme.BackgroundDark
import com.example.interview_ai.theme.BorderSubtle
import com.example.interview_ai.theme.Error
import com.example.interview_ai.theme.Primary
import com.example.interview_ai.theme.PrimaryGlow
import com.example.interview_ai.theme.Success
import com.example.interview_ai.theme.SurfaceDark
import com.example.interview_ai.theme.SurfaceVariantDark
import com.example.interview_ai.theme.TextMuted
import com.example.interview_ai.theme.TextPrimary
import com.example.interview_ai.theme.TextSecondary
import com.example.interview_ai.theme.Warning
import com.example.interview_ai.ui.components.AppTextField
import com.example.interview_ai.ui.components.AppTopBar
import com.example.interview_ai.ui.components.SecondaryButton
import com.example.interview_ai.ui.components.SurfaceCard
import com.example.interview_ai.ui.navigation.Routes
import com.example.interview_ai.viewmodel.HistoryViewModel

@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val BackgroundDark = colorScheme.background
    val SurfaceDark = colorScheme.surface
    val BorderSubtle = colorScheme.outlineVariant
    val TextPrimary = colorScheme.onBackground
    val TextSecondary = colorScheme.onSurfaceVariant
    val TextMuted = colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val PrimaryGlow = colorScheme.primary.copy(alpha = 0.16f)
    var sessionPendingDeletion by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadHistorySessions()
    }

    sessionPendingDeletion?.let { sessionId ->
        AlertDialog(
            onDismissRequest = { sessionPendingDeletion = null },
            title = {
                Text(
                    text = "Delete report?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "This permanently removes this interview evaluation from your history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSession(sessionId)
                        sessionPendingDeletion = null
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(AppRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Error,
                        contentColor = TextPrimary
                    )
                ) {
                    Text("Delete", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            },
            dismissButton = {
                SecondaryButton(
                    text = "Cancel",
                    onClick = { sessionPendingDeletion = null },
                    modifier = Modifier.width(100.dp)
                )
            },
            shape = RoundedCornerShape(AppRadius.lg),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Session History",
                subtitle = "Your previous mock interview evaluations",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
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
                .padding(AppSpacing.lg)
        ) {
            uiState.deleteErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = Error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = AppSpacing.sm)
                )
            }
            // Search Input Field
            AppTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = "",
                placeholder = "Search sessions by role...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = TextMuted
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Filter horizontal segment chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                val filters = listOf("All", "Technical", "Behavioral")
                filters.forEach { filter ->
                    val isSelected = uiState.selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceDark,
                                shape = RoundedCornerShape(AppRadius.md)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Primary else BorderSubtle,
                                shape = RoundedCornerShape(AppRadius.md)
                            )
                            .clickable { viewModel.onFilterChanged(filter) }
                            .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.sm),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // History Records List
            SurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cornerRadius = AppRadius.lg,
                padding = AppSpacing.sm
            ) {
                when {
                    uiState.isLoading -> HistoryLoadingSkeleton()
                    uiState.isError -> HistoryErrorContent(
                        message = uiState.errorMessage,
                        onRetry = { viewModel.refresh() }
                    )
                    uiState.sessions.isEmpty() -> HistoryEmptyContent(
                        onStart = {
                            navController.navigate(Routes.Interview.route)
                        }
                    )
                    uiState.filteredSessions.isEmpty() -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "No results",
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        Text(
                            text = "No sessions match your search.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                    else -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        uiState.filteredSessions.forEachIndexed { index, session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(
                                            Routes.Report.createRoute(session.id)
                                        )
                                    }
                                    .padding(AppSpacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Session Report Icon",
                                        tint = TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.md))
                                    Column {
                                        Text(
                                            text = session.role,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = session.date,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }

                                val badgeColor = when {
                                    session.score >= 85 -> Success
                                    session.score >= 70 -> Warning
                                    else -> Error
                                }
                                Box(
                                    modifier = Modifier
                                        .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(AppRadius.full))
                                        .border(1.dp, badgeColor, RoundedCornerShape(AppRadius.full))
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${session.score}/100",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = badgeColor
                                    )
                                }

                                IconButton(
                                    onClick = { sessionPendingDeletion = session.id },
                                    enabled = uiState.deletingSessionId != session.id
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete ${session.role} report",
                                        tint = if (uiState.deletingSessionId == session.id) TextMuted else Error
                                    )
                                }
                            }

                            if (index < uiState.filteredSessions.size - 1) {
                                HorizontalDivider(
                                    color = BorderSubtle,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(horizontal = AppSpacing.md)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryLoadingSkeleton() {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "historyShimmer")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(1000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.md)
            .graphicsLayer(alpha = alphaAnim),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        repeat(5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(AppRadius.sm))
                            .background(colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.md))
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(AppRadius.full))
                                .background(colorScheme.onSurface.copy(alpha = 0.1f))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.3f)
                                .height(9.dp)
                                .clip(RoundedCornerShape(AppRadius.full))
                                .background(colorScheme.onSurface.copy(alpha = 0.06f))
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(width = 52.dp, height = 20.dp)
                        .clip(RoundedCornerShape(AppRadius.full))
                        .background(colorScheme.onSurface.copy(alpha = 0.08f))
                )
            }
            if (it < 4) HorizontalDivider(color = colorScheme.outlineVariant, thickness = 1.dp)
        }
    }
}

@Composable
fun HistoryEmptyContent(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(AppRadius.full)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "No History",
                tint = Primary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        Text(
            text = "No sessions yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            text = "Complete your first mock interview to start\nbuilding your practice portfolio.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppSpacing.xl))
        Box(
            modifier = Modifier
                .background(
                    color = Primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(AppRadius.md)
                )
                .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(AppRadius.md))
                .clickable { onStart() }
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.md)
        ) {
            Text(
                text = "Start My First Interview",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Primary
            )
        }
    }
}

@Composable
fun HistoryErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Error.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(AppRadius.full)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Error",
                tint = Error,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        Text(
            text = "Couldn't load history",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            text = "Check your connection and try again.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppSpacing.xl))
        Box(
            modifier = Modifier
                .background(
                    color = Error.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(AppRadius.md)
                )
                .border(1.dp, Error.copy(alpha = 0.4f), RoundedCornerShape(AppRadius.md))
                .clickable { onRetry() }
                .padding(horizontal = AppSpacing.xl, vertical = AppSpacing.md)
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Error
            )
        }
    }
}
