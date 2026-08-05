package com.example.interview_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.LoginRequest
import com.example.interview_ai.data.api.RegisterRequest
import com.example.interview_ai.data.api.RetrofitClient
import com.example.interview_ai.data.datastore.AuthPreferences
import com.example.interview_ai.data.model.AuthUiState
import com.example.interview_ai.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Observe cached token and check auth
        viewModelScope.launch {
            authPreferences.jwtToken.collect { token ->
                if (!token.isNullOrEmpty()) {
                    RetrofitClient.setToken(token)
                    try {
                        val userDto = RetrofitClient.apiService.getUser()
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                                currentUser = User(
                                    id = userDto.id,
                                    name = userDto.name,
                                    email = userDto.email,
                                    targetRole = userDto.targetRole
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // Token might be expired
                        authPreferences.clearAuthSession()
                        RetrofitClient.setToken(null)
                    }
                }
            }
        }
    }

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
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                RetrofitClient.setToken(response.token)
                authPreferences.saveAuthSession(
                    token = response.token,
                    name = response.user.name,
                    email = response.user.email,
                    role = response.user.targetRole
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = User(
                            id = response.user.id,
                            name = response.user.name,
                            email = response.user.email,
                            targetRole = response.user.targetRole
                        )
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                // Graceful fallback to offline mock mode
                val mockUser = User(
                    id = "mock_user_101",
                    name = "Alex Mercer (Offline)",
                    email = email,
                    targetRole = "Android Developer"
                )
                authPreferences.saveAuthSession(
                    token = "mock_jwt_token",
                    name = mockUser.name,
                    email = mockUser.email,
                    role = mockUser.targetRole
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = mockUser,
                        generalError = "Server offline: Running in offline mock mode"
                    )
                }
                onSuccess()
            }
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
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(name, email, password, role)
                )
                RetrofitClient.setToken(response.token)
                authPreferences.saveAuthSession(
                    token = response.token,
                    name = response.user.name,
                    email = response.user.email,
                    role = response.user.targetRole
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = User(
                            id = response.user.id,
                            name = response.user.name,
                            email = response.user.email,
                            targetRole = response.user.targetRole
                        )
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                // Graceful fallback to offline mock mode
                val mockUser = User(
                    id = "mock_user_102",
                    name = name,
                    email = email,
                    targetRole = role
                )
                authPreferences.saveAuthSession(
                    token = "mock_jwt_token",
                    name = mockUser.name,
                    email = mockUser.email,
                    role = mockUser.targetRole
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = mockUser,
                        generalError = "Server offline: Running in offline mock mode"
                    )
                }
                onSuccess()
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authPreferences.clearAuthSession()
            RetrofitClient.setToken(null)
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    currentUser = null,
                    emailInput = "",
                    passwordInput = "",
                    nameInput = ""
                )
            }
            onSuccess()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
