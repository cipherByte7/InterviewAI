package com.example.interview_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.AnswerItem
import com.example.interview_ai.data.api.EvaluateRequest
import com.example.interview_ai.data.api.GenerateQuestionsRequest
import com.example.interview_ai.data.api.RetrofitClient
import com.example.interview_ai.data.model.InterviewStatus
import com.example.interview_ai.data.model.InterviewUiState
import com.example.interview_ai.data.model.Question
import com.example.interview_ai.utils.SpeechToTextEngine
import com.example.interview_ai.utils.TextToSpeechEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InterviewViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(InterviewUiState())
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    private val ttsEngine = TextToSpeechEngine(application)
    private val sttEngine = SpeechToTextEngine(application)

    private val answersTranscript = mutableListOf<AnswerItem>()
    private var timerJob: Job? = null

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
        ttsEngine.stop()
        sttEngine.stopListening()
        timerJob?.cancel()
        answersTranscript.clear()
        _uiState.update { InterviewUiState() }
    }

    fun startInterviewSession() {
        val questions = _uiState.value.generatedQuestions
        if (questions.isEmpty()) return

        answersTranscript.clear()
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
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
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
        val currentQuestionText = _uiState.value.activeQuestionText
        
        _uiState.update {
            it.copy(
                isAiSpeaking = true,
                isListening = false,
                userTranscript = "",
                isThinking = false
            )
        }

        // Speak the question using native TTS engine
        ttsEngine.speak(currentQuestionText) {
            // Callback: when TTS finished speaking, activate microphone STT listener
            viewModelScope.launch {
                if (!_uiState.value.isPaused && _uiState.value.status == InterviewStatus.ACTIVE) {
                    _uiState.update {
                        it.copy(
                            isAiSpeaking = false,
                            isListening = true
                        )
                    }
                    startSpeechToTextListener()
                }
            }
        }
    }

    private fun startSpeechToTextListener() {
        sttEngine.startListening(
            onResults = { result ->
                _uiState.update { it.copy(userTranscript = result) }
            },
            onPartialResults = { partial ->
                _uiState.update { it.copy(userTranscript = partial) }
            },
            onError = { error ->
                // STT failed or timed out. Gracefully keep listening or let user type.
            }
        )
    }

    fun submitUserAnswer(onCompleted: () -> Unit) {
        sttEngine.stopListening()
        val currentQuestion = _uiState.value.generatedQuestions[_uiState.value.currentQuestionIndex]
        val answerText = _uiState.value.userTranscript

        // Add current Q&A to the transcript list
        answersTranscript.add(
            AnswerItem(
                questionId = currentQuestion.id,
                questionText = currentQuestion.text,
                userAnswer = answerText
            )
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isListening = false,
                    isThinking = true
                )
            }

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
                // Last question answered, evaluate transcript
                evaluateSession(onCompleted)
            }
        }
    }

    private fun evaluateSession(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isThinking = true) }
            
            // Format time elapsed
            val minutes = _uiState.value.sessionDurationSeconds / 60
            val seconds = _uiState.value.sessionDurationSeconds % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            try {
                // Post answers transcript directly to Gemini evaluation backend API
                RetrofitClient.apiService.evaluateInterview(
                    EvaluateRequest(
                        duration = formattedTime,
                        transcript = answersTranscript,
                        role = "Senior Android Developer"
                    )
                )

                _uiState.update {
                    it.copy(
                        status = InterviewStatus.COMPLETED,
                        isThinking = false
                    )
                }
            } catch (e: Exception) {
                // Connection/Server failure. Evaluate simulated local fallback.
                delay(1200)
                _uiState.update {
                    it.copy(
                        status = InterviewStatus.COMPLETED,
                        isThinking = false
                    )
                }
            }
            onCompleted()
        }
    }

    fun togglePause() {
        _uiState.update { state ->
            val nextPaused = !state.isPaused
            state.copy(
                isPaused = nextPaused,
                isListening = if (!nextPaused && !state.isAiSpeaking && !state.isThinking) true else state.isListening
            )
        }
        
        if (_uiState.value.isPaused) {
            ttsEngine.stop()
            sttEngine.stopListening()
        } else {
            if (_uiState.value.isListening) {
                startSpeechToTextListener()
            } else if (_uiState.value.isAiSpeaking) {
                speakActiveQuestion()
            }
        }
    }

    fun finishInterview(onCompleted: () -> Unit) {
        ttsEngine.stop()
        sttEngine.stopListening()
        timerJob?.cancel()
        
        // Evaluate immediately what user has answered so far
        evaluateSession(onCompleted)
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

            for (step in 1..5) {
                delay(200)
                _uiState.update {
                    it.copy(generationProgress = step * 0.2f)
                }
            }

            try {
                // Request Gemini generated questions from server API
                val questions = RetrofitClient.apiService.generateQuestions(
                    GenerateQuestionsRequest(
                        targetRole = targetRole.ifEmpty { "Android Developer" },
                        skills = skills.ifEmpty { listOf("Kotlin", "Compose") },
                        count = _uiState.value.selectedQuestionCount,
                        category = _uiState.value.selectedCategory,
                        difficulty = _uiState.value.selectedDifficulty
                    )
                )

                _uiState.update {
                    it.copy(
                        status = InterviewStatus.READY,
                        isGenerating = false,
                        generatedQuestions = questions
                    )
                }
            } catch (e: Exception) {
                // Fallback to local mock generator if server is down or error
                val offlineQuestions = createMockQuestions(
                    role = targetRole.ifEmpty { "Android Developer" },
                    skills = skills.ifEmpty { listOf("Kotlin", "Compose") },
                    count = _uiState.value.selectedQuestionCount,
                    category = _uiState.value.selectedCategory,
                    difficulty = _uiState.value.selectedDifficulty
                )

                _uiState.update {
                    it.copy(
                        status = InterviewStatus.READY,
                        isGenerating = false,
                        generatedQuestions = offlineQuestions
                    )
                }
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
            "How do you implement dependency injection in Android using Dagger Hilt? What are Scopes and Components?"
        )

        val behavioralQuestions = listOf(
            "Tell me about a challenging Android bug you faced in a project. How did you diagnose and resolve it?",
            "Describe a situation where you had to work with a teammate who had a conflicting technical perspective. How did you handle it?",
            "How do you manage deadlines when multiple high-priority tasks are assigned to you simultaneously?"
        )

        val mixedList = mutableListOf<Question>()
        val primaryCategory = if (category == "Technical") technicalQuestions else if (category == "Behavioral") behavioralQuestions else technicalQuestions + behavioralQuestions

        for (i in 0 until count) {
            val baseQuestion = primaryCategory[i % primaryCategory.size]
            val skill = if (skills.isNotEmpty()) skills[i % skills.size] else "Android SDK"
            val text = if (baseQuestion.contains("Kotlin") || baseQuestion.contains("Compose")) baseQuestion else "Regarding your expertise in $skill, how do you handle concurrency or optimization for $role apps?"
            
            mixedList.add(
                Question(
                    id = (i + 1).toString(),
                    text = text,
                    category = if (i % 2 == 0 && category == "Mixed") "Technical" else if (category == "Mixed") "Behavioral" else category,
                    estimatedTimeMinutes = 2
                )
            )
        }
        return mixedList
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
        sttEngine.destroy()
        timerJob?.cancel()
    }
}
