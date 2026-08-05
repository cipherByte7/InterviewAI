package com.example.interview_ai.data.model

data class EvaluationDimension(
    val title: String,
    val score: Int,
    val description: String
)

data class EvaluationReport(
    val id: String = "",
    val role: String = "",
    val date: String = "",
    val overallScore: Int = 0,
    val duration: String = "",
    val dimensions: List<EvaluationDimension> = emptyList(),
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val suggestion: String = ""
)
