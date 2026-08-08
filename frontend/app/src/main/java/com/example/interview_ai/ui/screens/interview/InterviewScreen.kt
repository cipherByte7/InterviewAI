package com.example.interview_ai.ui.screens.interview

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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.scale
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.interview_ai.data.model.InterviewStatus
import com.example.interview_ai.data.model.InterviewState
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
import com.example.interview_ai.viewmodel.DashboardViewModel
import com.example.interview_ai.viewmodel.InterviewViewModel

@Composable
fun InterviewScreen(
    navController: NavHostController,
    interviewViewModel: InterviewViewModel,
    dashboardViewModel: DashboardViewModel
) {

    val uiState by interviewViewModel.uiState.collectAsState()
    val dashState by dashboardViewModel.uiState.collectAsState()
    val targetRole = dashState.parsedResume?.parsedRole ?: dashState.targetRole
    val skills = dashState.parsedResume?.skills ?: emptyList()
    val resumeName = dashState.uploadedResumeName

    val context = LocalContext.current

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

    var hasAudioPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            interviewViewModel.startInterviewSession(targetRole, skills) { reportId ->
                navController.navigate(com.example.interview_ai.ui.navigation.Routes.Report.createRoute(reportId)) {
                    popUpTo(com.example.interview_ai.ui.navigation.Routes.Interview.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (uiState.status == InterviewStatus.CONFIGURING) {
                AppTopBar(
                    title = "Configure AI Session",
                    subtitle = "Setup mock interview parameters",
                    onBackClick = { navController.popBackStack() }
                )
            }
        },
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
            when (uiState.status) {
                InterviewStatus.CONFIGURING -> {
                    ConfiguringContent(
                        difficulty = uiState.selectedDifficulty,
                        category = uiState.selectedCategory,
                        count = uiState.selectedQuestionCount,
                        resumeName = resumeName,
                        targetRole = targetRole,
                        onDifficultyChange = { interviewViewModel.setDifficulty(it) },
                        onCategoryChange = { interviewViewModel.setCategory(it) },
                        onCountChange = { interviewViewModel.setQuestionCount(it) },
                        onGenerate = {
                            if (hasAudioPermission) {
                                interviewViewModel.startInterviewSession(targetRole, skills) { reportId ->
                                    navController.navigate(com.example.interview_ai.ui.navigation.Routes.Report.createRoute(reportId)) {
                                        popUpTo(com.example.interview_ai.ui.navigation.Routes.Interview.route) { inclusive = true }
                                    }
                                }
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    )
                }
                InterviewStatus.ACTIVE -> {
                    if (!hasAudioPermission) {
                        PermissionDeniedContent(
                            onRequestPermission = { permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO) }
                        )
                    } else {
                        ActiveInterviewContent(
                            uiState = uiState,
                            onPauseToggle = { interviewViewModel.togglePause() },
                            onFinish = {
                                interviewViewModel.finishInterview { reportId ->
                                    navController.navigate(com.example.interview_ai.ui.navigation.Routes.Report.createRoute(reportId)) {
                                        popUpTo(com.example.interview_ai.ui.navigation.Routes.Interview.route) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
                else -> {
                    // Completed state handled by navigation redirect
                }
            }
        }
    }
}

@Composable
fun ConfiguringContent(
    difficulty: String,
    category: String,
    count: Int,
    resumeName: String?,
    targetRole: String,
    onDifficultyChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onCountChange: (Int) -> Unit,
    onGenerate: () -> Unit
) {

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
    val AccentCyan = colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppSpacing.lg)
    ) {
        // Resume Tailor Context Notification Banner
        SurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (resumeName != null) AccentCyan else BorderSubtle,
            padding = AppSpacing.md
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Resume Status",
                    tint = if (resumeName != null) AccentCyan else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.md))
                Column {
                    Text(
                        text = if (resumeName != null) "Resume Context Active" else "No Resume Uploaded",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = if (resumeName != null) "Adapting questions to $resumeName" else "Generate standard role-based mock questions",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Target Role Card
        SurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            padding = AppSpacing.md
        ) {
            Column {
                Text(
                    text = "TARGET ENGINEER ROLE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = targetRole,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Category Selector Chips
        Text(
            text = "Interview Type Focus",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            listOf("Technical", "Behavioral", "Mixed").forEach { cat ->
                val isSelected = cat == category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceDark,
                            shape = RoundedCornerShape(AppRadius.md)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Primary else BorderSubtle,
                            shape = RoundedCornerShape(AppRadius.md)
                        )
                        .clickable { onCategoryChange(cat) }
                        .padding(vertical = AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Primary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Difficulty Selector Chips
        Text(
            text = "Complexity Level",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            listOf("Junior", "Mid-Level", "Senior").forEach { diff ->
                val isSelected = diff == difficulty
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) Success.copy(alpha = 0.15f) else SurfaceDark,
                            shape = RoundedCornerShape(AppRadius.md)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Success else BorderSubtle,
                            shape = RoundedCornerShape(AppRadius.md)
                        )
                        .clickable { onDifficultyChange(diff) }
                        .padding(vertical = AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = diff,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) Success else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Question count Selector Chips
        Text(
            text = "Adaptive Questions Limit",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            listOf(3, 5, 8).forEach { cnt ->
                val isSelected = cnt == count
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) AccentCyan.copy(alpha = 0.15f) else SurfaceDark,
                            shape = RoundedCornerShape(AppRadius.md)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AccentCyan else BorderSubtle,
                            shape = RoundedCornerShape(AppRadius.md)
                        )
                        .clickable { onCountChange(cnt) }
                        .padding(vertical = AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$cnt Questions",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) AccentCyan else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        PrimaryButton(
            text = "Start Mock Interview",
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ActiveInterviewContent(
    uiState: com.example.interview_ai.data.model.InterviewUiState,
    onPauseToggle: () -> Unit,
    onFinish: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val Primary = colorScheme.primary
    val TextPrimary = colorScheme.onBackground
    val TextSecondary = colorScheme.onSurfaceVariant
    val TextMuted = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val BorderSubtle = colorScheme.outlineVariant
    val AccentCyan = colorScheme.secondary
    val SurfaceDark = colorScheme.surface

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (uiState.interviewState == InterviewState.AI_SPEAKING || uiState.interviewState == InterviewState.LISTENING) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val minutes = uiState.sessionDurationSeconds / 60
    val seconds = uiState.sessionDurationSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val stateColor = when (uiState.interviewState) {
        InterviewState.GREETING, InterviewState.AI_SPEAKING -> Primary
        InterviewState.LISTENING -> Success
        InterviewState.SILENCE_DETECTION -> Warning
        InterviewState.PROCESSING, InterviewState.AI_THINKING -> AccentCyan
        else -> BorderSubtle
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Active session header details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Primary.copy(alpha = 0.2f), RoundedCornerShape(AppRadius.sm))
                    .border(0.5.dp, Primary, RoundedCornerShape(AppRadius.sm))
                    .padding(horizontal = AppSpacing.sm, vertical = 2.dp)
            ) {
                Text(
                    text = "Adaptive Session Mode",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
            }
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(72.dp))

        // Pulsing audio visualizer orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            // Pulse circle backdrop
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                stateColor.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Core center orb
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(SurfaceDark, CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(stateColor, AccentCyan)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (uiState.interviewState) {
                        InterviewState.LISTENING -> Icons.Default.PlayArrow
                        InterviewState.SILENCE_DETECTION -> Icons.Default.Refresh
                        else -> Icons.Default.Person
                    },
                    contentDescription = "Status Icon",
                    tint = stateColor,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Speaking / Listening status string
        Text(
            text = when {
                uiState.isPaused -> "Interview Paused"
                uiState.interviewState == InterviewState.GREETING -> "🤖 AI greeting you..."
                uiState.interviewState == InterviewState.AI_SPEAKING -> "🤖 AI speaking question..."
                uiState.interviewState == InterviewState.LISTENING -> "🎤 Listening..."
                uiState.interviewState == InterviewState.SILENCE_DETECTION -> "⏳ Processing silence..."
                uiState.interviewState == InterviewState.PROCESSING -> "🤖 Submitting answer..."
                uiState.interviewState == InterviewState.AI_THINKING -> "🤖 Analyzing your answer..."
                else -> "Active Mock Session"
            },
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = stateColor
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Real-time user speech recognition transcript preview
        SurfaceCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            padding = AppSpacing.md
        ) {
            Column {
                Text(
                    text = "LIVE TRANSCRIPT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(AppSpacing.sm))
                
                val transcriptText = when (uiState.interviewState) {
                    InterviewState.AI_THINKING -> "AI is preparing the next adaptive question based on your experience..."
                    InterviewState.PROCESSING -> "Submitting transcript to Gemini evaluation engines..."
                    else -> uiState.userTranscript.ifEmpty { "Answer naturally. Your transcribed words will display here..." }
                }
                
                Text(
                    text = transcriptText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.userTranscript.isEmpty() && uiState.interviewState != InterviewState.AI_THINKING && uiState.interviewState != InterviewState.PROCESSING) TextMuted else TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Bottom control action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause Toggle Button
            SecondaryButton(
                text = if (uiState.isPaused) "Resume" else "Pause",
                onClick = onPauseToggle,
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Refresh,
                        contentDescription = "Pause Control",
                        tint = TextPrimary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        // Conclude interview Early exit trigger button
        Text(
            text = "End session early",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Error,
            modifier = Modifier
                .clickable { onFinish() }
                .padding(AppSpacing.sm)
        )
    }
}

@Composable
fun PermissionDeniedContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Microphone Required",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(AppSpacing.md))
        Text(
            text = "Microphone Access Required",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            text = "InterviewAI conducts mock sessions using audio speech inputs. Please grant audio recording permissions to practice.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        PrimaryButton(
            text = "Grant Permission",
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
