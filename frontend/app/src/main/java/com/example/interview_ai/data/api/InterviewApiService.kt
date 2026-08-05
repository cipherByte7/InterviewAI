package com.example.interview_ai.data.api

import com.example.interview_ai.data.model.EvaluationReport
import com.example.interview_ai.data.model.ParsedResume
import com.example.interview_ai.data.model.Question
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Request/Response DTOs
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val targetRole: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val targetRole: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class ParseResumeRequest(
    val resumeText: String,
    val fileName: String
)

data class GenerateQuestionsRequest(
    val targetRole: String,
    val skills: List<String>,
    val count: Int,
    val category: String,
    val difficulty: String
)

data class AnswerItem(
    val questionId: String,
    val questionText: String,
    val userAnswer: String
)

data class EvaluateRequest(
    val duration: String,
    val transcript: List<AnswerItem>,
    val role: String
)

interface InterviewApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/auth/user")
    suspend fun getUser(): UserDto

    @POST("api/resume/parse")
    suspend fun parseResume(@Body request: ParseResumeRequest): ParsedResume

    @POST("api/interview/generate")
    suspend fun generateQuestions(@Body request: GenerateQuestionsRequest): List<Question>

    @POST("api/interview/evaluate")
    suspend fun evaluateInterview(@Body request: EvaluateRequest): EvaluationReport

    @GET("api/interview/history")
    suspend fun getHistory(): List<EvaluationReport>
}
