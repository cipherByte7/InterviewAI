package com.example.interview_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.model.InterviewSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val sessions: List<InterviewSession> = emptyList(),
    val filteredSessions: List<InterviewSession> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "All", // All, Technical, Behavioral
    val isLoading: Boolean = false
)

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistorySessions()
    }

    fun loadHistorySessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Simulated local DB records
            val dummySessions = listOf(
                InterviewSession("1", "Android Developer (L4)", "Aug 04, 2026", 88),
                InterviewSession("2", "Kotlin Backend Developer", "Aug 01, 2026", 76),
                InterviewSession("3", "Mobile Engineer Intern", "Jul 28, 2026", 92),
                InterviewSession("4", "Senior Java Engineer", "Jul 15, 2026", 64),
                InterviewSession("5", "Behavioral Mock Session", "Jul 10, 2026", 85)
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    sessions = dummySessions,
                    filteredSessions = dummySessions
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFilterChanged(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase()
        val filter = _uiState.value.selectedFilter

        val filtered = _uiState.value.sessions.filter { session ->
            val matchesQuery = session.role.lowercase().contains(query)
            val matchesFilter = when (filter) {
                "Technical" -> !session.role.lowercase().contains("behavioral")
                "Behavioral" -> session.role.lowercase().contains("behavioral")
                else -> true
            }
            matchesQuery && matchesFilter
        }

        _uiState.update { it.copy(filteredSessions = filtered) }
    }
}
