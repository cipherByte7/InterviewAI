package com.example.interview_ai.data.model

data class Question(
    val id: String,
    val text: String,
    val category: String, // e.g. "Technical", "Behavioral", "Situational"
    val estimatedTimeMinutes: Int = 2
)
