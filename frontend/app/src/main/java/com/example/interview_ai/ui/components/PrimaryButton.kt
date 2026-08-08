package com.example.interview_ai.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "buttonScale")

    val colorScheme = MaterialTheme.colorScheme

    Button(
        onClick = { if (!isLoading && enabled) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(AppRadius.md),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.primary.copy(alpha = 0.35f),
            disabledContentColor = colorScheme.onPrimary.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = AppSpacing.lg, vertical = AppSpacing.md)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = colorScheme.onPrimary,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (enabled) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.5f)
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(AppSpacing.sm))
                    trailingIcon()
                }
            }
        }
    }
}
