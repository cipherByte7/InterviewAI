package com.example.interview_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.ParseResumeRequest
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
                        readinessScore = avgScore,
                        totalSessions = historyReports.size,
                        totalHours = historyReports.size * 0.15f, // Approx 9-10 mins per session
                        recentInterviews = recentSessions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Connection fail or unauthorized. Let's load standard offline defaults.
                _uiState.update {
                    it.copy(
                        userDisplayName = "Alex Mercer",
                        targetRole = "Android Developer",
                        readinessScore = 78,
                        totalSessions = 2,
                        totalHours = 0.5f,
                        recentInterviews = listOf(
                            InterviewSession("1", "Android Developer", "Aug 04, 2026", 78)
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun uploadResume(fileName: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUploading = true,
                    uploadProgress = 0.0f,
                    uploadedResumeName = null,
                    parsedResume = null
                )
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
            
            parseResume(fileName)
        }
    }

    private fun parseResume(fileName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsing = true) }
            try {
                // Read mock file content to send to server
                val dummyResumeText = "Alex Mercer resume. Experienced Kotlin Android developer. Skills: Jetpack Compose, Coroutines, Flow, Hilt, MVVM architecture, Git."
                
                val parsed = RetrofitClient.apiService.parseResume(
                    ParseResumeRequest(resumeText = dummyResumeText, fileName = fileName)
                )

                _uiState.update {
                    it.copy(
                        isParsing = false,
                        parsedResume = parsed
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isParsing = false,
                        parsedResume = ParsedResume(
                            parsedRole = "Senior Android Dev",
                            experienceYears = 3,
                            skills = listOf("Kotlin", "Jetpack Compose", "Coroutines", "Dagger Hilt", "Clean Architecture"),
                            education = "B.Tech in Computer Science",
                            projectsCount = 4,
                            isConfirmed = false
                        )
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
    }
}
