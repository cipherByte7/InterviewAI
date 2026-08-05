package com.example.interview_ai.data.model

data class AuthUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val nameInput: String = "",
    val targetRoleInput: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val targetRoleError: String? = null,
    val generalError: String? = null,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null
)
