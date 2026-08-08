package com.example.interview_ai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.interview_ai.theme.AppRadius
import com.example.interview_ai.theme.AppSpacing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "secondaryButtonScale")

    val colorScheme = MaterialTheme.colorScheme

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        enabled = enabled,
        shape = RoundedCornerShape(AppRadius.md),
        interactionSource = interactionSource,
        border = BorderStroke(1.dp, if (enabled) colorScheme.outline else colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface,
            disabledContainerColor = colorScheme.surface.copy(alpha = 0.5f),
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.4f)
        ),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(AppSpacing.sm))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (enabled) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
