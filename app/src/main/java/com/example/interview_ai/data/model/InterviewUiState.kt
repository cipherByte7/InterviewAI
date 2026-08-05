package com.example.interview_ai.data.model

enum class InterviewStatus {
    CONFIGURING,
    GENERATING,
    READY,
    ACTIVE,
    COMPLETED
}

data class InterviewUiState(
    val status: InterviewStatus = InterviewStatus.CONFIGURING,
    val selectedDifficulty: String = "Mid-Level",
    val selectedCategory: String = "Technical",
    val selectedQuestionCount: Int = 5,
    val generatedQuestions: List<Question> = emptyList(),
    val isGenerating: Boolean = false,
    val generationProgress: Float = 0f,
    val errorMessage: String? = null,
    val currentQuestionIndex: Int = 0,
    val activeQuestionText: String = "",
    val isAiSpeaking: Boolean = false,
    val isListening: Boolean = false,
    val userTranscript: String = "",
    val isThinking: Boolean = false,
    val sessionDurationSeconds: Int = 0,
    val isPaused: Boolean = false
)
