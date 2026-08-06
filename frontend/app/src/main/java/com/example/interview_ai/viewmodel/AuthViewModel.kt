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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        restoreSession()
    }

    /**
     * On every app start:
     * 1. Read the cached token, name, email, role from DataStore immediately
     *    so the profile shows real data even before a network round-trip.
     * 2. If a token exists, also try to refresh it from the server to pick up
     *    any server-side changes (updated target role, new stats, etc.).
     */
    private fun restoreSession() {
        viewModelScope.launch {
            val token = authPreferences.jwtToken.first()
            val name  = authPreferences.userName.first()
            val email = authPreferences.userEmail.first()
            val role  = authPreferences.targetRole.first()

            if (!token.isNullOrBlank()) {
                // Immediately populate UI from cache — no network needed
                RetrofitClient.setToken(token)
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentUser = User(
                            id = "",
                            name = name.ifBlank { "User" },
                            email = email,
                            targetRole = role
                        )
                    )
                }

                // Then try to sync fresh data from the server
                try {
                    val userDto = RetrofitClient.apiService.getUser()
                    // Persist any server-side changes back to DataStore
                    authPreferences.saveAuthSession(
                        token = token,
                        name  = userDto.name,
                        email = userDto.email,
                        role  = userDto.targetRole
                    )
                    _uiState.update {
                        it.copy(
                            currentUser = User(
                                id          = userDto.id,
                                name        = userDto.name,
                                email       = userDto.email,
                                targetRole  = userDto.targetRole
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Server unreachable — keep displaying the cached data, no error shown
                }
            }
        }
    }

    // ─── Input handlers ────────────────────────────────────────────────────────

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(emailInput = email, emailError = null, generalError = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordInput = password, passwordError = null, generalError = null) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(nameInput = name, nameError = null, generalError = null) }
    }

    fun onTargetRoleChanged(role: String) {
        _uiState.update { it.copy(targetRoleInput = role, targetRoleError = null, generalError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    // ─── Login ─────────────────────────────────────────────────────────────────

    fun login(onSuccess: () -> Unit) {
        val state    = _uiState.value
        val email    = state.emailInput.trim()
        val password = state.passwordInput.trim()

        var emailErr: String?    = null
        var passwordErr: String? = null
        var valid = true

        if (email.isEmpty()) { emailErr = "Email address is required"; valid = false }
        else if (!isEmailValid(email)) { emailErr = "Please enter a valid email address"; valid = false }
        if (password.isEmpty()) { passwordErr = "Password is required"; valid = false }
        else if (password.length < 6) { passwordErr = "Password must be at least 6 characters"; valid = false }

        if (!valid) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passwordErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                RetrofitClient.setToken(response.token)
                authPreferences.saveAuthSession(
                    token = response.token,
                    name  = response.user.name,
                    email = response.user.email,
                    role  = response.user.targetRole
                )
                _uiState.update {
                    it.copy(
                        isLoading       = false,
                        isAuthenticated = true,
                        generalError    = null,
                        currentUser     = User(
                            id         = response.user.id,
                            name       = response.user.name,
                            email      = response.user.email,
                            targetRole = response.user.targetRole
                        )
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = if (e is retrofit2.HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                        json.get("msg")?.asString ?: "Authentication failed"
                    } catch (_: Exception) {
                        "Authentication failed"
                    }
                } else {
                    "Unable to connect to server. Please check your internet connection and try again."
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = errorMsg
                    )
                }
            }
        }
    }

    // ─── Register ──────────────────────────────────────────────────────────────

    fun register(onSuccess: () -> Unit) {
        val state    = _uiState.value
        val name     = state.nameInput.trim()
        val role     = state.targetRoleInput.trim()
        val email    = state.emailInput.trim()
        val password = state.passwordInput.trim()

        var nameErr: String?       = null
        var roleErr: String?       = null
        var emailErr: String?      = null
        var passwordErr: String?   = null
        var valid = true

        if (name.isEmpty())     { nameErr     = "Full name is required";            valid = false }
        if (role.isEmpty())     { roleErr     = "Target role is required";          valid = false }
        if (email.isEmpty())    { emailErr    = "Email address is required";        valid = false }
        else if (!isEmailValid(email)) { emailErr = "Please enter a valid email";   valid = false }
        if (password.isEmpty()) { passwordErr = "Password is required";             valid = false }
        else if (password.length < 6) { passwordErr = "Minimum 6 characters";      valid = false }

        if (!valid) {
            _uiState.update {
                it.copy(
                    nameError       = nameErr,
                    targetRoleError = roleErr,
                    emailError      = emailErr,
                    passwordError   = passwordErr
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
                    name  = response.user.name,
                    email = response.user.email,
                    role  = response.user.targetRole
                )
                _uiState.update {
                    it.copy(
                        isLoading       = false,
                        isAuthenticated = true,
                        generalError    = null,
                        currentUser     = User(
                            id         = response.user.id,
                            name       = response.user.name,
                            email      = response.user.email,
                            targetRole = response.user.targetRole
                        )
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = if (e is retrofit2.HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                        json.get("msg")?.asString ?: "Registration failed"
                    } catch (_: Exception) {
                        "Registration failed"
                    }
                } else {
                    "Unable to connect to server. Please check your internet connection and try again."
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        generalError = errorMsg
                    )
                }
            }
        }
    }

    // ─── Logout ────────────────────────────────────────────────────────────────

    fun logout(onSuccess: () -> Unit) {
        println("AuthViewModel: Logout started")
        
        // Fire backend invalidate call asynchronously in background
        viewModelScope.launch {
            try {
                println("AuthViewModel: Background api logout started...")
                RetrofitClient.apiService.logout()
                println("AuthViewModel: Background api logout completed")
            } catch (e: Exception) {
                println("AuthViewModel: Background api logout failed (expected/offline): ${e.message}")
            }
        }

        // Run local session clearing immediately on the main thread
        viewModelScope.launch {
            println("AuthViewModel: Clearing local auth session in preferences...")
            authPreferences.clearAuthSession()
            println("AuthViewModel: Setting Retrofit token to null...")
            RetrofitClient.setToken(null)
            println("AuthViewModel: Resetting UI state...")
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    currentUser     = null,
                    emailInput      = "",
                    passwordInput   = "",
                    nameInput       = "",
                    generalError    = null
                )
            }
            println("AuthViewModel: Invoking onSuccess callback...")
            onSuccess()
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun isEmailValid(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
