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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.scale
import com.example.interview_ai.data.model.Question
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.interview_ai.data.model.InterviewStatus
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

    Scaffold(
        topBar = {
            if (uiState.status != InterviewStatus.GENERATING && uiState.status != InterviewStatus.ACTIVE) {
                AppTopBar(
                    title = if (uiState.status == InterviewStatus.READY) "Review Mock Prep" else "Configure AI Session",
                    subtitle = if (uiState.status == InterviewStatus.READY) " tailor-made interview questions" else "Setup mock interview parameters",
                    onBackClick = {
                        if (uiState.status == InterviewStatus.READY) {
                            interviewViewModel.resetInterview()
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryGlow,
                            BackgroundDark,
                            BackgroundDark
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
                        onGenerate = { interviewViewModel.generateQuestions(targetRole, skills) }
                    )
                }
                InterviewStatus.GENERATING -> {
                    GeneratingContent(progress = uiState.generationProgress)
                }
                InterviewStatus.READY -> {
                    ReadyContent(
                        questions = uiState.generatedQuestions,
                        role = targetRole,
                        difficulty = uiState.selectedDifficulty,
                        onReconfigure = { interviewViewModel.resetInterview() },
                        onStart = {
                            interviewViewModel.startInterviewSession()
                        }
                    )
                }
                InterviewStatus.ACTIVE -> {
                    ActiveInterviewContent(
                        uiState = uiState,
                        onPauseToggle = { interviewViewModel.togglePause() },
                        onSubmit = {
                            interviewViewModel.submitUserAnswer {
                                navController.navigate(com.example.interview_ai.ui.navigation.Routes.Report.route) {
                                    popUpTo(com.example.interview_ai.ui.navigation.Routes.Interview.route) { inclusive = true }
                                }
                            }
                        },
                        onFinish = {
                            interviewViewModel.finishInterview {
                                navController.navigate(com.example.interview_ai.ui.navigation.Routes.Report.route) {
                                    popUpTo(com.example.interview_ai.ui.navigation.Routes.Interview.route) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                else -> {
                    // Completed screens
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
                    imageVector = Icons.Default.Info,
                    contentDescription = "Resume context note",
                    tint = if (resumeName != null) AccentCyan else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppSpacing.md))
                Text(
                    text = if (resumeName != null) "Tailoring interview to active resume: $resumeName"
                           else "General mode. Upload resume on Dashboard to unlock tailored questions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (resumeName != null) TextPrimary else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        // Target Role Preview Info
        Text(
            text = "Target Role",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = targetRole,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Primary
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        // 1. Difficulty Level Option Group
        Text(
            text = "Experience Level",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            val levels = listOf("Junior", "Mid-Level", "Senior")
            levels.forEach { level ->
                val isSelected = difficulty == level
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
                        .clickable { onDifficultyChange(level) }
                        .padding(vertical = AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) TextPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        // 2. Question Category Option Group
        Text(
            text = "Interview Focus",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Column(
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            val categories = listOf("Technical", "Behavioral", "Mixed (Tech + Behavioral)")
            categories.forEach { cat ->
                val label = cat.split(" ").first()
                val isSelected = category == label
                SurfaceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryChange(label) },
                    borderColor = if (isSelected) Primary else BorderSubtle,
                    backgroundColor = if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceDark,
                    padding = AppSpacing.md
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Active Selection",
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        // 3. Question Count Selection
        Text(
            text = "Session Length",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            val counts = listOf(5, 10, 15)
            counts.forEach { qCount ->
                val isSelected = count == qCount
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
                        .clickable { onCountChange(qCount) }
                        .padding(vertical = AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$qCount Questions",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) TextPrimary else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.xxl))

        PrimaryButton(
            text = "Generate Tailored Questions",
            onClick = onGenerate
        )
    }
}

@Composable
fun GeneratingContent(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing star emblem
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(SurfaceVariantDark, CircleShape)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(colors = listOf(Primary, AccentCyan)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "AI Star icon",
                tint = Primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        Text(
            text = "Tailoring Mock Questions",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = "Gemini is analyzing skills and setting difficulty...",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(AppSpacing.xl))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(6.dp),
            color = Primary,
            trackColor = BorderSubtle,
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            text = "${(progress * 100).toInt()}% Generated",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun ReadyContent(
    questions: List<Question>,
    role: String,
    difficulty: String,
    onReconfigure: () -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.lg)
    ) {
        Text(
            text = "Tailored Questions Ready",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = "$role ($difficulty)",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Scrollable List of questions
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            questions.forEach { q ->
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
                                text = "Question ${q.id}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Primary
                            )
                            Box(
                                modifier = Modifier
                                    .background(AccentCyan.copy(alpha = 0.15f), RoundedCornerShape(AppRadius.full))
                                    .border(0.5.dp, AccentCyan, RoundedCornerShape(AppRadius.full))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = q.category,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = AccentCyan
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.sm))
                        Text(
                            text = q.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Bottom Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            SecondaryButton(
                text = "Configure",
                onClick = onReconfigure,
                modifier = Modifier.weight(0.4f)
            )
            PrimaryButton(
                text = "Start audio mock",
                onClick = onStart,
                modifier = Modifier.weight(0.6f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Launch Icon",
                        tint = TextPrimary
                    )
                }
            )
        }
    }
}

@Composable
fun ActiveInterviewContent(
    uiState: com.example.interview_ai.data.model.InterviewUiState,
    onPauseToggle: () -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (uiState.isAiSpeaking || uiState.isListening) 1.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val minutes = uiState.sessionDurationSeconds / 60
    val seconds = uiState.sessionDurationSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

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
                    text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.selectedQuestionCount}",
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

        Spacer(modifier = Modifier.height(54.dp))

        // Pulsing audio visualizer orb
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            // Pulse circle backdrop
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (uiState.isAiSpeaking) Primary.copy(alpha = 0.4f)
                                else if (uiState.isListening) Success.copy(alpha = 0.4f)
                                else BorderSubtle,
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Core center orb
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(SurfaceDark, CircleShape)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                if (uiState.isAiSpeaking) Primary
                                else if (uiState.isListening) Success
                                else BorderSubtle,
                                if (uiState.isAiSpeaking) AccentCyan
                                else if (uiState.isListening) AccentCyan
                                else BorderSubtle
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (uiState.isAiSpeaking) Icons.Default.Star
                                  else if (uiState.isListening) Icons.Default.PlayArrow
                                  else Icons.Default.Refresh,
                    contentDescription = "Status Icon",
                    tint = if (uiState.isAiSpeaking) Primary
                           else if (uiState.isListening) Success
                           else TextMuted,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Speaking / Listening status string
        Text(
            text = when {
                uiState.isPaused -> "Interview Paused"
                uiState.isAiSpeaking -> "AI Speaking..."
                uiState.isListening -> "Listening to your answer..."
                uiState.isThinking -> "AI analyzing response..."
                else -> "Active Mock Session"
            },
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (uiState.isListening) Success else if (uiState.isAiSpeaking) Primary else TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Question display card
        SurfaceCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (uiState.isAiSpeaking) Primary else BorderSubtle,
            padding = AppSpacing.md
        ) {
            Column {
                Text(
                    text = "AI INTERVIEWER",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = uiState.activeQuestionText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.lg))

        // Real-time user speech recognition transcript preview
        SurfaceCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            padding = AppSpacing.md
        ) {
            Column {
                Text(
                    text = "YOUR TRANSCRIPT (REAL-TIME)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(AppSpacing.xs))
                Text(
                    text = uiState.userTranscript.ifEmpty { "Speech text will appear here as you answer..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.userTranscript.isEmpty()) TextMuted else TextPrimary,
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

            // Submit Answer button
            PrimaryButton(
                text = if (uiState.currentQuestionIndex + 1 == uiState.selectedQuestionCount) "Submit & Finish" else "Submit Answer",
                onClick = onSubmit,
                modifier = Modifier.weight(1.3f),
                enabled = uiState.userTranscript.isNotEmpty() && !uiState.isAiSpeaking && !uiState.isPaused && !uiState.isThinking,
                isLoading = uiState.isThinking
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        // Early End session exit button
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