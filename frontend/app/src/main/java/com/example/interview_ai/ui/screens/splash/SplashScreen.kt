package com.example.interview_ai.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.interview_ai.theme.AccentCyan
import com.example.interview_ai.theme.AppSpacing
import com.example.interview_ai.theme.BackgroundDark
import com.example.interview_ai.theme.Primary
import com.example.interview_ai.theme.PrimaryGlow
import com.example.interview_ai.theme.SurfaceVariantDark
import com.example.interview_ai.theme.TextMuted
import com.example.interview_ai.theme.TextPrimary
import com.example.interview_ai.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme
    val BackgroundDark = colorScheme.background
    val SurfaceVariantDark = colorScheme.surfaceVariant
    val TextPrimary = colorScheme.onBackground
    val TextMuted = colorScheme.onSurfaceVariant
    val PrimaryGlow = colorScheme.primary.copy(alpha = 0.16f)

    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600)
        )
        delay(1400)
        navController.navigate(Routes.Login.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PrimaryGlow,
                        BackgroundDark,
                        BackgroundDark
                    ),
                    radius = 1200f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Glowing Emblem
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = SurfaceVariantDark,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(colorScheme.primary, AccentCyan)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "InterviewAI Emblem",
                    modifier = Modifier.size(44.dp),
                    tint = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            Text(
                text = "InterviewAI",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            Text(
                text = "AI-Powered Career Co-Pilot",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = TextMuted
            )
        }
    }
}
