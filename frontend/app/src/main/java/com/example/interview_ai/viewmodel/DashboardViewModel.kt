package com.example.interview_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.ParseResumeRequest
import com.example.interview_ai.data.api.UpdateProfileRequest
import com.example.interview_ai.data.api.RetrofitClient
import com.example.interview_ai.data.datastore.AuthPreferences
import com.example.interview_ai.data.model.DashboardUiState
import com.example.interview_ai.data.model.InterviewSession
import com.example.interview_ai.data.model.ParsedResume
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val authPreferences = AuthPreferences(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Fetch target role from local cache
                val savedRole = authPreferences.targetRole.first()
                
                // Fetch user data
                val userDto = RetrofitClient.apiService.getUser()

                // Fetch history from backend
                val historyReports = RetrofitClient.apiService.getHistory()
                
                val recentSessions = historyReports.take(4).map { report ->
                    InterviewSession(
                        id = report.id,
                        role = report.role,
                        date = report.date,
                        score = report.overallScore
                    )
                }

                val avgScore = if (historyReports.isNotEmpty()) {
                    historyReports.map { it.overallScore }.average().toInt()
                } else {
                    0
                }

                _uiState.update {
                    it.copy(
                        userDisplayName = userDto.name,
                        targetRole = savedRole.ifEmpty { userDto.targetRole },
                        readinessScore = userDto.stats?.averageScore ?: avgScore,
                        totalSessions = userDto.stats?.totalSessions ?: historyReports.size,
                        totalHours = userDto.stats?.totalHours ?: (historyReports.size * 0.15f),
                        recentInterviews = recentSessions,
                        parsedResume = userDto.parsedResume,
                        uploadedResumeName = userDto.parsedResume?.uploadedResumeName,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Connection fail or unauthorized — load from cached DataStore values
                val cachedName = authPreferences.userName.first().ifBlank { "User" }
                val cachedRole = authPreferences.targetRole.first()
                _uiState.update {
                    it.copy(
                        userDisplayName = cachedName,
                        targetRole = cachedRole,
                        readinessScore = 0,
                        totalSessions = 0,
                        totalHours = 0f,
                        recentInterviews = emptyList(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateTargetRole(newRole: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(targetRole = newRole) }
            authPreferences.saveTargetRole(newRole)
            try {
                RetrofitClient.apiService.updateProfile(UpdateProfileRequest(newRole))
            } catch (e: Exception) {
                // Connection fail/offline
            }
        }
    }

    fun uploadResume(context: android.content.Context, uri: android.net.Uri, fileName: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploading = true,
                    uploadProgress = 0.0f,
                    uploadedResumeName = null,
                    parsedResume = null,
                    parseError = false,
                    parseErrorMessage = ""
                )
            }
            
            val fileBytes = try {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (e: Exception) {
                null
            }

            for (progress in 1..5) {
                delay(200)
                _uiState.update {
                    it.copy(uploadProgress = progress * 0.2f)
                }
            }
            
            _uiState.update {
                it.copy(
                    isUploading = false,
                    uploadedResumeName = fileName,
                    uploadProgress = 1.0f
                )
            }
            
            if (fileBytes != null && fileBytes.isNotEmpty()) {
                parseResume(fileBytes, fileName)
            } else {
                _uiState.update {
                    it.copy(
                        isParsing = false,
                        parseError = true,
                        parseErrorMessage = "Could not read the PDF file. Please try a different file."
                    )
                }
            }
        }
    }

    private fun parseResume(bytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsing = true, parseError = false, parseErrorMessage = "") }
            try {
                val mediaType = "application/pdf".toMediaTypeOrNull()
                val requestFile = bytes.toRequestBody(mediaType)
                val body = okhttp3.MultipartBody.Part.createFormData("file", fileName, requestFile)
                
                val parsed = RetrofitClient.apiService.parseResume(body)

                _uiState.update {
                    it.copy(
                        isParsing = false,
                        parsedResume = parsed,
                        parseError = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isParsing = false,
                        parseError = true,
                        parseErrorMessage = "Failed to parse resume. The file may be password-protected, corrupted, or in an unsupported format."
                    )
                }
            }
        }
    }

    fun confirmParsedResume() {
        viewModelScope.launch {
            val currentResume = _uiState.value.parsedResume
            if (currentResume != null) {
                val updatedResume = currentResume.copy(isConfirmed = true)
                authPreferences.saveTargetRole(updatedResume.parsedRole)
                _uiState.update {
                    it.copy(
                        parsedResume = updatedResume,
                        targetRole = updatedResume.parsedRole
                    )
                }
                // Persist confirmation to the server
                try {
                    RetrofitClient.apiService.confirmResume()
                } catch (_: Exception) {
                    // Server unreachable — local state is already updated
                }
            }
        }
    }

    fun removeResume() {
        _uiState.update {
            it.copy(
                uploadedResumeName = null,
                uploadProgress = 0f,
                isUploading = false,
                isParsing = false,
                parsedResume = null
            )
        }
        // Also clear from the server so the user doc stays consistent
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteResume()
            } catch (_: Exception) {
                // Server unreachable — local state already cleared
            }
        }
    }
}
