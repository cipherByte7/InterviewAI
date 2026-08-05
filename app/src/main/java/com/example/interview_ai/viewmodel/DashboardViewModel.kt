package com.example.interview_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.model.DashboardUiState
import com.example.interview_ai.data.model.InterviewSession
import com.example.interview_ai.data.model.ParsedResume
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulating loading data from API / database
            _uiState.update {
                it.copy(
                    userDisplayName = "Alex Mercer",
                    targetRole = "Android Engineer",
                    readinessScore = 84,
                    totalSessions = 12,
                    totalHours = 4.5f,
                    recentInterviews = listOf(
                        InterviewSession("1", "Android Developer (L4)", "Aug 04, 2026", 88),
                        InterviewSession("2", "Kotlin Backend Developer", "Aug 01, 2026", 76),
                        InterviewSession("3", "Mobile Engineer Intern", "Jul 28, 2026", 92)
                    ),
                    isLoading = false
                )
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
            
            // Simulate incremental upload progress (e.g. 20% steps)
            for (progress in 1..5) {
                delay(300)
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

            // Immediately trigger parsing once upload is completed
            parseResume()
        }
    }

    private fun parseResume() {
        viewModelScope.launch {
            _uiState.update { it.copy(isParsing = true) }
            delay(1500) // Simulate AI parsing delay
            
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

    fun confirmParsedResume() {
        _uiState.update { state ->
            val updatedResume = state.parsedResume?.copy(isConfirmed = true)
            state.copy(
                parsedResume = updatedResume,
                targetRole = updatedResume?.parsedRole ?: state.targetRole
            )
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

