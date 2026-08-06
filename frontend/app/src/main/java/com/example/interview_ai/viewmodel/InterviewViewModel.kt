package com.example.interview_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.AnswerItem
import com.example.interview_ai.data.api.EvaluateRequest
import com.example.interview_ai.data.api.StartInterviewRequest
import com.example.interview_ai.data.api.NextQuestionRequest
import com.example.interview_ai.data.api.ConversationItem
import com.example.interview_ai.data.api.RetrofitClient
import com.example.interview_ai.data.model.InterviewStatus
import com.example.interview_ai.data.model.InterviewState
import com.example.interview_ai.data.model.InterviewUiState
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
    private val conversationHistory = mutableListOf<ConversationItem>()
    private var timerJob: Job? = null
    private var silenceDetectionJob: Job? = null
    private var onSessionCompleted: (() -> Unit)? = null

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
        silenceDetectionJob?.cancel()
        answersTranscript.clear()
        conversationHistory.clear()
        _uiState.update { InterviewUiState() }
    }

    fun startInterviewSession(targetRole: String, skills: List<String>, onCompleted: () -> Unit) {
        this.onSessionCompleted = onCompleted
        answersTranscript.clear()
        conversationHistory.clear()

        _uiState.update {
            it.copy(
                status = InterviewStatus.ACTIVE,
                interviewState = InterviewState.GREETING,
                sessionDurationSeconds = 0,
                activeQuestionText = "",
                userTranscript = "",
                isPaused = false
            )
        }

        // Start duration timer
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

        // Speak the initial greeting
        val greetingText = "Hello. Welcome to today's interview. Let's begin."
        _uiState.update {
            it.copy(
                interviewState = InterviewState.AI_SPEAKING,
                activeQuestionText = greetingText
            )
        }

        ttsEngine.speak(greetingText) {
            viewModelScope.launch {
                if (!_uiState.value.isPaused && _uiState.value.status == InterviewStatus.ACTIVE) {
                    fetchFirstQuestion(targetRole)
                }
            }
        }
    }

    private suspend fun fetchFirstQuestion(targetRole: String) {
        _uiState.update { it.copy(interviewState = InterviewState.AI_THINKING) }
        try {
            val response = RetrofitClient.apiService.startInterview(
                StartInterviewRequest(
                    targetRole = targetRole.ifEmpty { "Android Developer" },
                    difficulty = _uiState.value.selectedDifficulty,
                    category = _uiState.value.selectedCategory
                )
            )
            val firstQuestion = response.question
            conversationHistory.add(ConversationItem(role = "interviewer", text = firstQuestion))
            
            _uiState.update {
                it.copy(
                    activeQuestionText = firstQuestion,
                    interviewState = InterviewState.AI_SPEAKING
                )
            }
            speakActiveQuestion()
        } catch (e: Exception) {
            // Offline fallback first question
            val fallbackQ = "Could you tell me about your background in mobile engineering and your preferred architecture patterns?"
            conversationHistory.add(ConversationItem(role = "interviewer", text = fallbackQ))
            _uiState.update {
                it.copy(
                    activeQuestionText = fallbackQ,
                    interviewState = InterviewState.AI_SPEAKING
                )
            }
            speakActiveQuestion()
        }
    }

    private fun speakActiveQuestion() {
        val currentQuestionText = _uiState.value.activeQuestionText
        _uiState.update {
            it.copy(
                interviewState = InterviewState.AI_SPEAKING,
                userTranscript = ""
            )
        }

        ttsEngine.speak(currentQuestionText) {
            viewModelScope.launch {
                if (!_uiState.value.isPaused && _uiState.value.status == InterviewStatus.ACTIVE) {
                    _uiState.update {
                        it.copy(
                            interviewState = InterviewState.LISTENING
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
                resetSilenceDetectionTimer()
            },
            onPartialResults = { partial ->
                _uiState.update { it.copy(userTranscript = partial) }
                resetSilenceDetectionTimer()
            },
            onError = { error ->
                val currentState = _uiState.value.interviewState
                if (currentState == InterviewState.LISTENING || currentState == InterviewState.SILENCE_DETECTION) {
                    if (error == 7 || error == 6) { // Timeout or No Match
                        if (_uiState.value.userTranscript.trim().isNotEmpty()) {
                            submitUserAnswer()
                        } else if (!_uiState.value.isPaused && _uiState.value.status == InterviewStatus.ACTIVE) {
                            startSpeechToTextListener()
                        }
                    } else if (error == 8) { // Busy
                        viewModelScope.launch {
                            sttEngine.stopListening()
                            delay(300)
                            if (!_uiState.value.isPaused && _uiState.value.status == InterviewStatus.ACTIVE) {
                                startSpeechToTextListener()
                            }
                        }
                    } else {
                        if (!_uiState.value.isPaused && _uiState.value.status == InterviewStatus.ACTIVE) {
                            startSpeechToTextListener()
                        }
                    }
                }
            }
        )
    }

    private fun resetSilenceDetectionTimer() {
        silenceDetectionJob?.cancel()
        if (_uiState.value.userTranscript.trim().isEmpty()) return

        silenceDetectionJob = viewModelScope.launch {
            _uiState.update { it.copy(interviewState = InterviewState.SILENCE_DETECTION) }
            delay(5000) // 5 seconds silence threshold
            if (_uiState.value.interviewState == InterviewState.SILENCE_DETECTION) {
                submitUserAnswer()
            }
        }
    }

    fun submitUserAnswer() {
        silenceDetectionJob?.cancel()
        sttEngine.stopListening()

        val answerText = _uiState.value.userTranscript
        val questionText = _uiState.value.activeQuestionText

        conversationHistory.add(ConversationItem(role = "candidate", text = answerText))
        answersTranscript.add(
            AnswerItem(
                questionId = "q_${answersTranscript.size + 1}",
                questionText = questionText,
                userAnswer = answerText
            )
        )

        _uiState.update {
            it.copy(
                interviewState = InterviewState.PROCESSING
            )
        }

        viewModelScope.launch {
            val limit = _uiState.value.selectedQuestionCount
            if (answersTranscript.size >= limit) {
                concludeInterview()
            } else {
                fetchNextQuestion(answerText)
            }
        }
    }

    private suspend fun fetchNextQuestion(currentAnswer: String) {
        _uiState.update { it.copy(interviewState = InterviewState.AI_THINKING) }
        delay(1500) // 1.5s thinking delay for realism

        try {
            val response = RetrofitClient.apiService.getNextQuestion(
                NextQuestionRequest(
                    targetRole = "Android Developer",
                    difficulty = _uiState.value.selectedDifficulty,
                    category = _uiState.value.selectedCategory,
                    conversationHistory = conversationHistory,
                    currentAnswer = currentAnswer
                )
            )

            if (response.isLastQuestion || response.nextQuestion.isBlank()) {
                concludeInterview()
            } else {
                conversationHistory.add(ConversationItem(role = "interviewer", text = response.nextQuestion))
                _uiState.update {
                    it.copy(
                        activeQuestionText = response.nextQuestion,
                        interviewState = InterviewState.AI_SPEAKING
                    )
                }
                speakActiveQuestion()
            }
        } catch (e: Exception) {
            // Offline fallback follow-up
            val offlineQ = "Can you share your experience implementing dependency injection, specifically describing custom scopes and components?"
            conversationHistory.add(ConversationItem(role = "interviewer", text = offlineQ))
            _uiState.update {
                it.copy(
                    activeQuestionText = offlineQ,
                    interviewState = InterviewState.AI_SPEAKING
                )
            }
            speakActiveQuestion()
        }
    }

    private fun concludeInterview() {
        val concludeText = "Thank you. That concludes today's interview."
        _uiState.update {
            it.copy(
                interviewState = InterviewState.AI_SPEAKING,
                activeQuestionText = concludeText
            )
        }

        ttsEngine.speak(concludeText) {
            viewModelScope.launch {
                evaluateSession {
                    onSessionCompleted?.invoke()
                }
            }
        }
    }

    private fun evaluateSession(onCompleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(interviewState = InterviewState.PROCESSING) }

            val minutes = _uiState.value.sessionDurationSeconds / 60
            val seconds = _uiState.value.sessionDurationSeconds % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)

            try {
                RetrofitClient.apiService.evaluateInterview(
                    EvaluateRequest(
                        duration = formattedTime,
                        transcript = answersTranscript,
                        role = "Android Developer",
                        difficulty = _uiState.value.selectedDifficulty,
                        category = _uiState.value.selectedCategory
                    )
                )

                _uiState.update {
                    it.copy(
                        status = InterviewStatus.COMPLETED,
                        interviewState = InterviewState.COMPLETED
                    )
                }
            } catch (e: Exception) {
                // Connection/Server failure fallback
                delay(1200)
                _uiState.update {
                    it.copy(
                        status = InterviewStatus.COMPLETED,
                        interviewState = InterviewState.COMPLETED
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
                interviewState = if (nextPaused) state.interviewState else state.interviewState
            )
        }

        if (_uiState.value.isPaused) {
            ttsEngine.stop()
            sttEngine.stopListening()
            silenceDetectionJob?.cancel()
        } else {
            val state = _uiState.value.interviewState
            if (state == InterviewState.LISTENING) {
                startSpeechToTextListener()
            } else if (state == InterviewState.AI_SPEAKING) {
                speakActiveQuestion()
            }
        }
    }

    fun finishInterview(onCompleted: () -> Unit) {
        this.onSessionCompleted = onCompleted
        ttsEngine.stop()
        sttEngine.stopListening()
        timerJob?.cancel()
        silenceDetectionJob?.cancel()
        evaluateSession(onCompleted)
    }

    // Legacy method triggers to prevent build errors
    fun generateQuestions(targetRole: String, skills: List<String>) {}

    override fun onCleared() {
        super.onCleared()
        ttsEngine.shutdown()
        sttEngine.destroy()
        timerJob?.cancel()
        silenceDetectionJob?.cancel()
    }
}
