package com.example.interview_ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interview_ai.data.api.RetrofitClient
import com.example.interview_ai.data.model.EvaluationDimension
import com.example.interview_ai.data.model.EvaluationReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportUiState(
    val report: EvaluationReport? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        //loadEvaluationReport()
    }

    fun loadEvaluationReport(reportId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Fetch reports list from backend API
                android.util.Log.d("REPORT_DEBUG", "ReportId = $reportId")
                val report = RetrofitClient.apiService.getReport(reportId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        report = report,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("REPORT_DEBUG", "Failed to load report", e)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    report = null,
                    errorMessage = e.message ?: "Failed to load report"
                )
            }
            }
        }
    }

    private fun loadFallbackReport() {
        _uiState.update {
            it.copy(
                isLoading = false,
                report = EvaluationReport(
                    id = "report_102",
                    role = "Senior Android Developer",
                    date = "Aug 05, 2026",
                    overallScore = 86,
                    duration = "08:45",
                    dimensions = listOf(
                        EvaluationDimension("Technical Accuracy", 88, "Strong understanding of Kotlin Coroutines launch/async concepts and Compose lifecycle recomposition steps."),
                        EvaluationDimension("Communication Clarity", 85, "Articulation was direct and concise. Avoided rambling and focused on engineering design trade-offs."),
                        EvaluationDimension("Depth of Knowledge", 90, "Detailed knowledge of ViewModelStore configuration change behaviors under the hood."),
                        EvaluationDimension("Confidence & Articulation", 82, "Spoke clearly but paced a bit quickly during advanced system architecture questions.")
                    ),
                    strengths = listOf(
                        "Accurately mapped the difference between fire-and-forget (launch) and result-seeking (async) concurrency triggers.",
                        "Described memory leak debugging flows detailing how LeakCanary isolates heap dumps.",
                        "Properly illustrated clean architecture layers decoupling domain layer models."
                    ),
                    weaknesses = listOf(
                        "Could expand further on recomposition optimization strategies using @Stable annotations.",
                        "Pacing was slightly hurried during discussions about Dagger Hilt Scopes."
                    ),
                    suggestion = "Practice speaking at a steadier cadence. Review Jetpack Compose stability optimizations and learn how to write custom Compose compiler stabilization rules."
                ),
                errorMessage = null
            )
        }
    }
}
