package com.example.interview_ai.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.interview_ai.theme.AppRadius
import com.example.interview_ai.theme.AppSpacing
import com.example.interview_ai.theme.BorderHighlight
import com.example.interview_ai.theme.BorderSubtle
import com.example.interview_ai.theme.Error
import com.example.interview_ai.theme.Primary
import com.example.interview_ai.theme.SurfaceDark
import com.example.interview_ai.theme.TextMuted
import com.example.interview_ai.theme.TextPrimary
import com.example.interview_ai.theme.TextSecondary

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    singleLine: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(AppSpacing.xs))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            },
            singleLine = singleLine,
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(AppRadius.md),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.15f),
                disabledContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.05f),
                errorContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.15f),
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outlineVariant,
                disabledBorderColor = colorScheme.outlineVariant.copy(alpha = 0.5f),
                errorBorderColor = colorScheme.error,
                focusedLeadingIconColor = colorScheme.primary,
                unfocusedLeadingIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                focusedTrailingIconColor = colorScheme.onSurface,
                unfocusedTrailingIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(AppSpacing.xs))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.error
            )
        }
    }
}
