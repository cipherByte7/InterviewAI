package com.example.interview_ai.data.model

enum class InterviewStatus {
    CONFIGURING,
    ACTIVE,
    COMPLETED
}

enum class InterviewState {
    IDLE,
    GREETING,
    AI_SPEAKING,
    LISTENING,
    SILENCE_DETECTION,
    PROCESSING,
    AI_THINKING,
    COMPLETED
}

data class InterviewUiState(
    val status: InterviewStatus = InterviewStatus.CONFIGURING,
    val interviewState: InterviewState = InterviewState.IDLE,
    val selectedDifficulty: String = "Mid-Level",
    val selectedCategory: String = "Technical",
    val selectedQuestionCount: Int = 5,
    val activeQuestionText: String = "",
    val userTranscript: String = "",
    val sessionDurationSeconds: Int = 0,
    val isPaused: Boolean = false,
    val errorMessage: String? = null,
    
    // Legacy support to prevent build errors
    val isGenerating: Boolean = false,
    val generationProgress: Float = 0f,
    val generatedQuestions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val isAiSpeaking: Boolean = false,
    val isListening: Boolean = false,
    val isThinking: Boolean = false
)
