package com.example.interview_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.model.InterviewStatus
import com.example.interview_ai.data.model.InterviewUiState
import com.example.interview_ai.data.model.Question
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InterviewViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InterviewUiState())
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    fun setDifficulty(difficulty: String) {
        _uiState.update { it.copy(selectedDifficulty = difficulty) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setQuestionCount(count: Int) {
        _uiState.update { it.copy(selectedQuestionCount = count) }
    }

    fun resetInterview() {
        _uiState.update { InterviewUiState() }
    }

    fun generateQuestions(targetRole: String, skills: List<String>) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    status = InterviewStatus.GENERATING,
                    isGenerating = true,
                    generationProgress = 0.0f
                )
            }

            // Simulate parsing progress steps
            for (step in 1..5) {
                delay(400)
                _uiState.update {
                    it.copy(generationProgress = step * 0.2f)
                }
            }

            // Mocked generation tailored to the target role and parsed skills
            val questions = createMockQuestions(
                role = if (targetRole.isNotEmpty()) targetRole else "Android Developer",
                skills = if (skills.isNotEmpty()) skills else listOf("Kotlin", "Android SDK"),
                count = _uiState.value.selectedQuestionCount,
                category = _uiState.value.selectedCategory,
                difficulty = _uiState.value.selectedDifficulty
            )

            _uiState.update {
                it.copy(
                    status = InterviewStatus.READY,
                    isGenerating = false,
                    generatedQuestions = questions
                )
            }
        }
    }

    private fun createMockQuestions(
        role: String,
        skills: List<String>,
        count: Int,
        category: String,
        difficulty: String
    ): List<Question> {
        val technicalQuestions = listOf(
            "Explain the difference between launch and async in Kotlin Coroutines. When would you use each?",
            "What is Jetpack Compose Recomposition? How can you optimize a Composable to prevent unnecessary recompositions?",
            "How does ViewModel survive configuration changes under the hood? Explain the role of ViewModelStore.",
            "Explain clean architecture layers in Android. Why should the domain layer be decoupled from framework details?",
            "What are Kotlin StateFlow and SharedFlow? Describe a scenario where SharedFlow is preferred over StateFlow.",
            "How do you implement dependency injection in Android using Dagger Hilt? What are Scopes and Components?",
            "Describe the lifecycle steps of a Composable function. What is SideEffect and DisposableEffect?",
            "What is Android's garbage collection mechanism, and how do you profile and debug memory leaks using LeakCanary?"
        )

        val behavioralQuestions = listOf(
            "Tell me about a challenging Android bug you faced in a project. How did you diagnose and resolve it?",
            "Describe a situation where you had to work with a teammate who had a conflicting technical perspective. How did you handle it?",
            "How do you manage deadlines when multiple high-priority tasks are assigned to you simultaneously?",
            "Tell me about a project where you implemented a new technology or architecture. What was the learning curve like?",
            "Describe a time when you received constructive feedback on your code quality. What steps did you take to address it?"
        )

        val mixedList = mutableListOf<Question>()
        var index = 1

        val primaryCategory = when (category) {
            "Technical" -> technicalQuestions
            "Behavioral" -> behavioralQuestions
            else -> technicalQuestions + behavioralQuestions
        }

        // Generate tailored questions
        for (i in 0 until count) {
            val baseQuestion = primaryCategory[i % primaryCategory.size]
            val tailoredText = if (category == "Technical" || category == "Mixed") {
                // Tailor it to the primary parsed skill if available
                val skill = skills[i % skills.size]
                if (baseQuestion.contains("Kotlin") || baseQuestion.contains("Compose") || baseQuestion.contains("Hilt")) {
                    baseQuestion
                } else {
                    "Regarding your expertise in $skill, how do you handle concurrency, state management, or optimization for $role apps?"
                }
            } else {
                baseQuestion
            }

            mixedList.add(
                Question(
                    id = "$index",
                    text = tailoredText,
                    category = if (i % 2 == 0 && category == "Mixed") "Technical" else if (category == "Mixed") "Behavioral" else category,
                    estimatedTimeMinutes = 2
                )
            )
            index++
        }

        return mixedList
    }
}
