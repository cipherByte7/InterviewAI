package com.example.interview_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.RetrofitClient
import com.example.interview_ai.data.model.InterviewSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val sessions: List<InterviewSession> = emptyList(),
    val filteredSessions: List<InterviewSession> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: String = "All", // All, Technical, Behavioral
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistorySessions()
    }

    fun loadHistorySessions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false, errorMessage = "") }
            try {
                // Query server API for all past sessions
                val reports = RetrofitClient.apiService.getHistory()
                val sessionItems = reports.map { report ->
                    InterviewSession(
                        id = report.id,
                        role = report.role,
                        category = report.category,
                        date = report.date,
                        score = report.overallScore
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        sessions = sessionItems,
                        filteredSessions = sessionItems
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = e.message ?: "Unable to reach server"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadHistorySessions()
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
            val matchesFilter = filter == "All" || session.category.equals(filter, ignoreCase = true)
            matchesQuery && matchesFilter
        }

        _uiState.update { it.copy(filteredSessions = filtered) }
    }
}
