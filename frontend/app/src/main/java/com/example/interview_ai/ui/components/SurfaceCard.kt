package com.example.interview_ai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.interview_ai.theme.AppRadius
import com.example.interview_ai.theme.AppSpacing
import androidx.compose.material3.MaterialTheme

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = AppRadius.lg,
    padding: Dp = AppSpacing.lg,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(cornerRadius),
            color = backgroundColor,
            border = BorderStroke(borderWidth, borderColor)
        ) {
            Box(modifier = Modifier.padding(padding), content = content)
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(cornerRadius),
            color = backgroundColor,
            border = BorderStroke(borderWidth, borderColor)
        ) {
            Box(modifier = Modifier.padding(padding), content = content)
        }
    }
}
