package com.example.interview_ai.ui.screens.dashboard

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
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
import com.example.interview_ai.theme.Error
import com.example.interview_ai.theme.Primary
import com.example.interview_ai.theme.Success
import com.example.interview_ai.theme.SurfaceDark
import com.example.interview_ai.theme.SurfaceVariantDark
import com.example.interview_ai.theme.TextMuted
import com.example.interview_ai.theme.TextPrimary
import com.example.interview_ai.theme.TextSecondary
import com.example.interview_ai.theme.Warning
import com.example.interview_ai.ui.components.SurfaceCard
import com.example.interview_ai.ui.navigation.Routes
import com.example.interview_ai.viewmodel.DashboardViewModel
import com.example.interview_ai.ui.components.BottomNavBar
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val BackgroundDark = colorScheme.background
    val SurfaceDark = colorScheme.surface
    val SurfaceVariantDark = colorScheme.surfaceVariant
    val BorderSubtle = colorScheme.outlineVariant
    val TextPrimary = colorScheme.onBackground
    val TextSecondary = colorScheme.onSurfaceVariant
    val TextMuted = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val Primary = colorScheme.primary
    val PrimaryGlow = colorScheme.primary.copy(alpha = 0.15f)
    var selectedItem by remember { mutableIntStateOf(0) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }
    
    val context = LocalContext.current
    var showUploadSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "resume.pdf"
            showUploadSheet = false  // Close sheet immediately before async upload begins
            viewModel.uploadResume(context, it, fileName)
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.lg)
        ) {
            // Header: Welcome & User Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${uiState.userDisplayName.split(" ").firstOrNull() ?: "Developer"}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.xs))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(AppRadius.sm))
                                .border(1.dp, Primary.copy(alpha = 0.4f), RoundedCornerShape(AppRadius.sm))
                                .padding(horizontal = AppSpacing.sm, vertical = 2.dp)
                        ) {
                            Text(
                                text = uiState.targetRole,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                        }
                    }
                }

                // Mini Avatar Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SurfaceVariantDark, CircleShape)
                        .border(1.dp, BorderSubtle, CircleShape)
                        .clickable { navController.navigate(Routes.Profile.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile avatar",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            // Overall Readiness Score Card
            SurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppRadius.lg,
                padding = AppSpacing.lg
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interview Readiness",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${uiState.readinessScore}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.sm))

                    LinearProgressIndicator(
                        progress = { uiState.readinessScore / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Primary,
                        trackColor = BorderSubtle,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.md))

                    Text(
                        text = "Good score! Keep practicing Android Architecture and Flow state questions to improve to 90%+",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            // Summary Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
            ) {
                SurfaceCard(
                    modifier = Modifier.weight(1f),
                    padding = AppSpacing.md
                ) {
                    Column {
                        Text(
                            text = "Sessions",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.totalSessions}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                }
                SurfaceCard(
                    modifier = Modifier.weight(1f),
                    padding = AppSpacing.md
                ) {
                    Column {
                        Text(
                            text = "Practice Hours",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.totalHours}h",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            // Action Items Header
            Text(
                text = "Launch Mock prep",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            // Quick Action Card 1: Mock Interview
            SurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Routes.Interview.route) },
                cornerRadius = AppRadius.lg,
                padding = AppSpacing.lg
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Primary.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, Primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Interview Icon",
                            tint = Primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(AppSpacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Interactive AI Interview",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Start audio interview tailored to your experience",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.md))

            // Quick Action Card 2: Upload Resume
            SurfaceCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showUploadSheet = true },
                borderColor = if (uiState.uploadedResumeName != null) AccentCyan else BorderSubtle,
                cornerRadius = AppRadius.lg,
                padding = AppSpacing.lg
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (uiState.uploadedResumeName != null) AccentCyan.copy(alpha = 0.2f)
                                else AccentCyan.copy(alpha = 0.15f),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (uiState.uploadedResumeName != null) AccentCyan else AccentCyan.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Resume Icon",
                            tint = AccentCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(AppSpacing.md))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.uploadedResumeName ?: "Upload Resume (PDF)",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (uiState.uploadedResumeName != null) "Active Resume • Manage or replace"
                                   else "Customize questions to fit your parsed resume content",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.uploadedResumeName != null) AccentCyan else TextMuted
                        )
                    }
                }
            }

            if (uiState.isParsing) {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
                val alphaAnim by infiniteTransition.animateFloat(
                    initialValue = 0.35f,
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                SurfaceCard(
                    modifier = Modifier.fillMaxWidth().alpha(alphaAnim),
                    borderColor = colorScheme.primary.copy(alpha = 0.4f),
                    padding = AppSpacing.lg
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.md))
                            Text(
                                text = "Extracting details with Gemini AI...",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height(16.dp)
                                .background(colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.35f)
                                .height(12.dp)
                                .background(colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
                            Box(modifier = Modifier.size(60.dp, 20.dp).background(colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(4.dp)))
                            Box(modifier = Modifier.size(80.dp, 20.dp).background(colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(4.dp)))
                            Box(modifier = Modifier.size(50.dp, 20.dp).background(colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(4.dp)))
                        }
                    }
                }
            }

            if (uiState.parseError) {
                Spacer(modifier = Modifier.height(AppSpacing.md))
                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Error,
                    padding = AppSpacing.lg
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Parse Error",
                                tint = Error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.sm))
                            Text(
                                text = "Resume Parsing Failed",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Error
                            )
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Text(
                            text = uiState.parseErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        Text(
                            text = "Remove & Try Again",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Error,
                            modifier = Modifier
                                .clickable { viewModel.removeResume() }
                                .padding(vertical = AppSpacing.xs)
                        )
                    }
                }
            }

            uiState.parsedResume?.let { parsed ->
                Spacer(modifier = Modifier.height(AppSpacing.md))
                SurfaceCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (parsed.isConfirmed) Success else Primary,
                    padding = AppSpacing.lg
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (parsed.isConfirmed) "✓ Profile Synchronized" else "AI Parsed Profile",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (parsed.isConfirmed) Success else Primary
                            )
                            if (!parsed.isConfirmed) {
                                Box(
                                    modifier = Modifier
                                        .background(Warning.copy(alpha = 0.2f), RoundedCornerShape(AppRadius.full))
                                        .border(1.dp, Warning, RoundedCornerShape(AppRadius.full))
                                        .padding(horizontal = AppSpacing.sm, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Pending Verify",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = Warning
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.md))

                        Text(
                            text = "Extracted Role: ${parsed.parsedRole}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Experience: ${parsed.experienceYears} Years | Education: ${parsed.education}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        Text(
                            text = "Extracted Skills:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            parsed.skills.chunked(3).forEach { row ->
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
                                                color = TextSecondary,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (!parsed.isConfirmed) {
                            Spacer(modifier = Modifier.height(AppSpacing.lg))
                            com.example.interview_ai.ui.components.PrimaryButton(
                                text = "Verify & Tailor Mock Questions",
                                onClick = { viewModel.confirmParsedResume() }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            // Recent Sessions
            Text(
                text = "Recent mock history",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            SurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppRadius.lg,
                padding = AppSpacing.sm
            ) {
                Column {
                    if (uiState.recentInterviews.isEmpty()) {
                        Text(
                            text = "No practice sessions yet. Start your first mock interview above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(AppSpacing.md)
                        )
                    } else {
                        uiState.recentInterviews.forEachIndexed { index, session ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(Routes.Report.createRoute(session.id))
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

                                // Score indicator badge
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
                            }

                            if (index < uiState.recentInterviews.size - 1) {
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

    if (showUploadSheet) {
        ModalBottomSheet(
            onDismissRequest = { showUploadSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = AppSpacing.md)
                        .size(width = 40.dp, height = 4.dp)
                        .background(BorderSubtle, CircleShape)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.xl)
                    .padding(bottom = AppSpacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upload Resume",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = "Add your PDF resume to customize AI questions",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(AppSpacing.xl))

                if (uiState.uploadedResumeName != null) {
                    // Uploaded resume preview
                    SurfaceCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = AccentCyan,
                        padding = AppSpacing.lg
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "PDF Icon",
                                    tint = AccentCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.md))
                                Column {
                                    Text(
                                        text = uiState.uploadedResumeName ?: "",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Custom tailoring active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Success
                                    )
                                }
                            }

                            // Delete button
                            IconButton(onClick = { viewModel.removeResume() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Resume",
                                    tint = Error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.xl))

                    com.example.interview_ai.ui.components.PrimaryButton(
                        text = "Done",
                        onClick = { showUploadSheet = false }
                    )
                } else if (uiState.isUploading) {
                    // Uploading state
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { uiState.uploadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = Primary,
                            trackColor = BorderSubtle,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.md))
                        Text(
                            text = "Uploading... ${(uiState.uploadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                } else {
                    // Drop zone upload
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(AppRadius.lg))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(AppRadius.lg))
                            .clickable { filePickerLauncher.launch("application/pdf") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(AppSpacing.lg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload Add Icon",
                                tint = Primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.md))
                            Text(
                                text = "Tap here to select PDF resume",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "PDF files up to 10MB",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}
