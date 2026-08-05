package com.example.interview_ai.data.model

data class ParsedResume(
    val parsedRole: String = "",
    val experienceYears: Int = 0,
    val skills: List<String> = emptyList(),
    val education: String = "",
    val projectsCount: Int = 0,
    val isConfirmed: Boolean = false
)
