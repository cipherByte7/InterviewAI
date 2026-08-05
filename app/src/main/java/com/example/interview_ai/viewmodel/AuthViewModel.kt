package com.example.interview_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.model.AuthUiState
import com.example.interview_ai.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                emailInput = email,
                emailError = null,
                generalError = null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                passwordInput = password,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun onNameChanged(name: String) {
        _uiState.update {
            it.copy(
                nameInput = name,
                nameError = null,
                generalError = null
            )
        }
    }

    fun onTargetRoleChanged(role: String) {
        _uiState.update {
            it.copy(
                targetRoleInput = role,
                targetRoleError = null,
                generalError = null
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun login(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput.trim()

        var isValid = true
        var emailErr: String? = null
        var passwordErr: String? = null

        if (email.isEmpty()) {
            emailErr = "Email address is required"
            isValid = false
        } else if (!isEmailValid(email)) {
            emailErr = "Please enter a valid email address"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordErr = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            isValid = false
        }

        if (!isValid) {
            _uiState.update {
                it.copy(
                    emailError = emailErr,
                    passwordError = passwordErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            delay(1200) // Simulate auth API call

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    currentUser = User(
                        id = "user_101",
                        name = "Alex Mercer",
                        email = email,
                        targetRole = "Android Engineer"
                    )
                )
            }
            onSuccess()
        }
    }

    fun register(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        val name = currentState.nameInput.trim()
        val role = currentState.targetRoleInput.trim()
        val email = currentState.emailInput.trim()
        val password = currentState.passwordInput.trim()

        var isValid = true
        var nameErr: String? = null
        var roleErr: String? = null
        var emailErr: String? = null
        var passwordErr: String? = null

        if (name.isEmpty()) {
            nameErr = "Full name is required"
            isValid = false
        }

        if (role.isEmpty()) {
            roleErr = "Target role is required"
            isValid = false
        }

        if (email.isEmpty()) {
            emailErr = "Email address is required"
            isValid = false
        } else if (!isEmailValid(email)) {
            emailErr = "Please enter a valid email address"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordErr = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordErr = "Password must be at least 6 characters"
            isValid = false
        }

        if (!isValid) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    targetRoleError = roleErr,
                    emailError = emailErr,
                    passwordError = passwordErr
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            delay(1200) // Simulate auth API call

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    currentUser = User(
                        id = "user_102",
                        name = name,
                        email = email,
                        targetRole = role
                    )
                )
            }
            onSuccess()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
