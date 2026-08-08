package com.example.interview_ai.data.model

import com.google.gson.annotations.SerializedName

data class EvaluationDimension(
    val title: String,
    val score: Int,
    val description: String
)

data class EvaluationReport(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val role: String = "",
    val category: String = "Technical",
    val date: String = "",
    val overallScore: Int = 0,
    val duration: String = "",
    val dimensions: List<EvaluationDimension> = emptyList(),
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val suggestion: String = ""
)

