package com.example.interview_ai.data.model

data class InterviewSession(
    val id: String,
    val role: String,
    val date: String,
    val score: Int
)

data class DashboardUiState(
    val userDisplayName: String = "",
    val targetRole: String = "",
    val readinessScore: Int = 0,
    val totalSessions: Int = 0,
    val totalHours: Float = 0.0f,
    val recentInterviews: List<InterviewSession> = emptyList(),
    val isLoading: Boolean = false,
    val uploadedResumeName: String? = null,
    val uploadProgress: Float = 0f,
    val isUploading: Boolean = false
)
