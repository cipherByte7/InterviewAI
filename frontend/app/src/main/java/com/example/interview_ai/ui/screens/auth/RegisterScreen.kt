package com.example.interview_ai.ui.screens.auth

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.interview_ai.theme.AccentCyan
import com.example.interview_ai.theme.AppRadius
import com.example.interview_ai.theme.AppSpacing
import com.example.interview_ai.theme.BackgroundDark
import com.example.interview_ai.theme.Primary
import com.example.interview_ai.theme.PrimaryGlow
import com.example.interview_ai.theme.SurfaceVariantDark
import com.example.interview_ai.theme.TextMuted
import com.example.interview_ai.theme.TextPrimary
import com.example.interview_ai.theme.TextSecondary
import com.example.interview_ai.ui.components.AppTextField
import com.example.interview_ai.ui.components.PrimaryButton
import com.example.interview_ai.ui.components.SurfaceCard
import com.example.interview_ai.ui.navigation.Routes
import com.example.interview_ai.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val BackgroundDark = colorScheme.background
    val TextPrimary = colorScheme.onBackground
    val TextSecondary = colorScheme.onSurfaceVariant
    val TextMuted = colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val PrimaryGlow = colorScheme.primary.copy(alpha = 0.16f)

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
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.xl)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo & Title
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = SurfaceVariantDark,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Primary, AccentCyan)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Emblem",
                    tint = Primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.lg))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(AppSpacing.xs))

            Text(
                text = "Start practicing with your AI mock interviewer",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            // Form Card
            SurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppRadius.xl,
                padding = AppSpacing.xl
            ) {
                Column {
                    AppTextField(
                        value = uiState.nameInput,
                        onValueChange = { authViewModel.onNameChanged(it) },
                        label = "Full Name",
                        placeholder = "Alex Mercer",
                        errorMessage = uiState.nameError,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name Icon",
                                tint = TextMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.lg))

                    AppTextField(
                        value = uiState.targetRoleInput,
                        onValueChange = { authViewModel.onTargetRoleChanged(it) },
                        label = "Target Role / Specialization",
                        placeholder = "e.g. Android Engineer / Fullstack Dev",
                        errorMessage = uiState.targetRoleError,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Role Icon",
                                tint = TextMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.lg))

                    AppTextField(
                        value = uiState.emailInput,
                        onValueChange = { authViewModel.onEmailChanged(it) },
                        label = "Email Address",
                        placeholder = "alex@example.com",
                        errorMessage = uiState.emailError,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = TextMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.lg))

                    AppTextField(
                        value = uiState.passwordInput,
                        onValueChange = { authViewModel.onPasswordChanged(it) },
                        label = "Password",
                        placeholder = "Minimum 6 characters",
                        errorMessage = uiState.passwordError,
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Icon",
                                tint = TextMuted
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = if (uiState.isPasswordVisible) "HIDE" else "SHOW",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Primary,
                                modifier = Modifier
                                    .clickable { authViewModel.togglePasswordVisibility() }
                                    .padding(AppSpacing.xs)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.xl))

                    if (uiState.generalError != null) {
                        Text(
                            text = uiState.generalError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = AppSpacing.sm)
                        )
                    }

                    PrimaryButton(
                        text = "Create Account",
                        onClick = {
                            authViewModel.register {
                                navController.navigate(Routes.Dashboard.route) {
                                    popUpTo(Routes.Login.route) { inclusive = true }
                                }
                            }
                        },
                        isLoading = uiState.isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.xl))

            // Footer Sign In Link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(AppSpacing.xs))
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Primary,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
