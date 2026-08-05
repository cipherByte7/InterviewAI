package com.example.interview_ai.ui.screens.report

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.interview_ai.theme.AccentCyan
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
import com.example.interview_ai.ui.components.AppTopBar
import com.example.interview_ai.ui.components.PrimaryButton
import com.example.interview_ai.ui.components.SecondaryButton
import com.example.interview_ai.ui.components.SurfaceCard
import com.example.interview_ai.ui.navigation.Routes
import com.example.interview_ai.viewmodel.ReportViewModel

@Composable
fun ReportScreen(
    navController: NavHostController,
    viewModel: ReportViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "AI Evaluation",
                subtitle = "Mock prep performance breakdown",
                onBackClick = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                    }
                }
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryGlow, BackgroundDark, BackgroundDark),
                        radius = 1200f
                    )
                )
        ) {
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = Primary)
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    Text(
                        text = "Analyzing transcript performance metrics...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            } else {
                uiState.report?.let { report ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(AppSpacing.lg)
                    ) {
                        // Overall Grade Header Card
                        SurfaceCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = Success,
                            padding = AppSpacing.lg
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = report.role,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Session Completed • ${report.date} (${report.duration})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(AppSpacing.md))
                                    Text(
                                        text = "Verdict: Strong technical concepts. Articulate speaking velocity to boost scores above 90%.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(AppSpacing.lg))

                                // Grade Ring Badge
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .background(Success.copy(alpha = 0.15f), CircleShape)
                                        .border(2.dp, Success, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${report.overallScore}",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Success
                                            ),
                                            lineHeight = 24.sp
                                        )
                                        Text(
                                            text = "SCORE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = Success
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.xl))

                        // Dimension progress grid
                        Text(
                            text = "Core Dimensions",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
                        ) {
                            report.dimensions.forEach { dim ->
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
                                            Text(
                                                text = dim.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${dim.score}/100",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Primary
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                                        LinearProgressIndicator(
                                            progress = { dim.score / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp),
                                            color = Primary,
                                            trackColor = BorderSubtle,
                                            strokeCap = StrokeCap.Round
                                        )

                                        Spacer(modifier = Modifier.height(AppSpacing.xs))

                                        Text(
                                            text = dim.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.xl))

                        // Strengths section
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Strengths Icon",
                                tint = Success,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.sm))
                            Text(
                                text = "Key Strengths",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        SurfaceCard(
                            modifier = Modifier.fillMaxWidth(),
                            padding = AppSpacing.md
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                report.strengths.forEach { strength ->
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Strength check",
                                            tint = Success,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(AppSpacing.sm))
                                        Text(
                                            text = strength,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.xl))

                        // Weaknesses section
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Weakness Icon",
                                tint = Warning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.sm))
                            Text(
                                text = "Areas of Improvement",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.sm))

                        SurfaceCard(
                            modifier = Modifier.fillMaxWidth(),
                            padding = AppSpacing.md
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                                report.weaknesses.forEach { weakness ->
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "•",
                                            color = Warning,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(horizontal = AppSpacing.xs)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = weakness,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.xl))

                        // Direct action plan
                        Text(
                            text = "Custom AI Action Plan",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceDark, RoundedCornerShape(AppRadius.md))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(AppRadius.md))
                                .padding(AppSpacing.md)
                        ) {
                            Text(
                                text = report.suggestion,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = AccentCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Bottom Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
                        ) {
                            SecondaryButton(
                                text = "Dashboard",
                                onClick = {
                                    navController.navigate(Routes.Dashboard.route) {
                                        popUpTo(Routes.Dashboard.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Dashboard link icon",
                                        tint = TextPrimary
                                    )
                                }
                            )

                            PrimaryButton(
                                text = "Practice Again",
                                onClick = {
                                    navController.navigate(Routes.Interview.route) {
                                        popUpTo(Routes.Interview.route) { inclusive = true }
                                    }
                                },
                                modifier = Modifier.weight(1.3f),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Restart practice icon",
                                        tint = TextPrimary
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}