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
    val errorMessage: String = "",
    val deletingSessionId: String? = null,
    val deleteErrorMessage: String? = null
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

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(deletingSessionId = sessionId, deleteErrorMessage = null) }
            try {
                val response = RetrofitClient.apiService.deleteReport(sessionId)
                if (!response.isSuccessful) {
                    throw IllegalStateException("Unable to delete this report")
                }

                _uiState.update { current ->
                    val remainingSessions = current.sessions.filterNot { it.id == sessionId }
                    current.copy(
                        sessions = remainingSessions,
                        filteredSessions = filterSessions(
                            remainingSessions,
                            current.searchQuery,
                            current.selectedFilter
                        ),
                        deletingSessionId = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        deletingSessionId = null,
                        deleteErrorMessage = e.message ?: "Couldn't delete this report. Please try again."
                    )
                }
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
        val current = _uiState.value
        _uiState.update {
            it.copy(filteredSessions = filterSessions(current.sessions, current.searchQuery, current.selectedFilter))
        }
    }

    private fun filterSessions(
        sessions: List<InterviewSession>,
        searchQuery: String,
        selectedFilter: String
    ): List<InterviewSession> {
        val query = searchQuery.lowercase()

        return sessions.filter { session ->
            val matchesQuery = session.role.lowercase().contains(query)
            val matchesFilter = selectedFilter == "All" || session.category.equals(selectedFilter, ignoreCase = true)
            matchesQuery && matchesFilter
        }
    }
}
