package com.example.interview_ai.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.interview_ai.data.preferences.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCyan,
    onSecondary = BackgroundDark,
    tertiary = AccentPurple,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderHighlight,
    error = Error,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary.copy(alpha = 0.14f),
    onPrimaryContainer = PrimaryVariant,
    secondary = AccentCyan,
    onSecondary = Color.White,
    tertiary = AccentPurple,
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF121826),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF121826),
    surfaceVariant = Color(0xFFEEF1F7),
    onSurfaceVariant = Color(0xFF536071),
    outline = Color(0xFFD4DAE5),
    outlineVariant = Color(0xFFE2E7EF),
    error = Error,
    onError = Color.White
)

@Composable
fun InterviewAITheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    Crossfade(targetState = colorScheme, animationSpec = tween(300), label = "themeTransition") { scheme ->
        MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
    }
}
