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

    fun startInterviewSession() {
        val questions = _uiState.value.generatedQuestions
        if (questions.isEmpty()) return

        _uiState.update {
            it.copy(
                status = InterviewStatus.ACTIVE,
                currentQuestionIndex = 0,
                activeQuestionText = questions[0].text,
                sessionDurationSeconds = 0,
                isPaused = false
            )
        }

        // Start timer
        viewModelScope.launch {
            while (_uiState.value.status == InterviewStatus.ACTIVE) {
                delay(1000)
                if (!_uiState.value.isPaused) {
                    _uiState.update {
                        it.copy(sessionDurationSeconds = it.sessionDurationSeconds + 1)
                    }
                }
            }
        }

        speakActiveQuestion()
    }

    private fun speakActiveQuestion() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiSpeaking = true,
                    isListening = false,
                    userTranscript = "",
                    isThinking = false
                )
            }
            delay(2500) // Simulate AI speaking the question

            if (!_uiState.value.isPaused) {
                _uiState.update {
                    it.copy(
                        isAiSpeaking = false,
                        isListening = true
                    )
                }
                simulateUserTranscription()
            }
        }
    }

    private fun simulateUserTranscription() {
        viewModelScope.launch {
            val transcriptBase = listOf(
                "I think Kotlin coroutines manage threading asynchronously. We use launch when we don't need a result, which is fire and forget, and async when we want to return a deferred value.",
                "Yes, Compose recomposition occurs when input parameters change. To optimize, we can mark classes stable or use remember to cache states, preventing unnecessary composition tree updates.",
                "In my past role, I worked on modularizing our application. We separated it into core, network, and feature modules. The main challenge was handling navigation dependencies."
            )
            
            val activeText = transcriptBase[_uiState.value.currentQuestionIndex % transcriptBase.size]
            val words = activeText.split(" ")
            var currentTranscript = ""

            for (word in words) {
                if (_uiState.value.status != InterviewStatus.ACTIVE || !_uiState.value.isListening || _uiState.value.isPaused) {
                    break
                }
                delay(180) // Simulate real-time word-by-word STT transcription
                currentTranscript = if (currentTranscript.isEmpty()) word else "$currentTranscript $word"
                _uiState.update { it.copy(userTranscript = currentTranscript) }
            }
        }
    }

    fun submitUserAnswer(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isListening = false,
                    isThinking = true
                )
            }
            delay(1800) // Simulate AI analyzing response and deciding on follow-up

            val currentIndex = _uiState.value.currentQuestionIndex
            val questions = _uiState.value.generatedQuestions

            if (currentIndex + 1 < questions.size) {
                _uiState.update {
                    it.copy(
                        currentQuestionIndex = currentIndex + 1,
                        activeQuestionText = questions[currentIndex + 1].text,
                        isThinking = false
                    )
                }
                speakActiveQuestion()
            } else {
                _uiState.update {
                    it.copy(
                        status = InterviewStatus.COMPLETED,
                        isThinking = false
                    )
                }
                onCompleted()
            }
        }
    }

    fun togglePause() {
        _uiState.update {
            val nextPaused = !it.isPaused
            it.copy(
                isPaused = nextPaused,
                // If resuming and was listening, trigger transcription simulation again
                isListening = if (!nextPaused && !it.isAiSpeaking && !it.isThinking) true else it.isListening
            )
        }
        
        if (!_uiState.value.isPaused && _uiState.value.isListening) {
            simulateUserTranscription()
        }
    }

    fun finishInterview(onCompleted: () -> Unit) {
        _uiState.update { it.copy(status = InterviewStatus.COMPLETED) }
        onCompleted()
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
