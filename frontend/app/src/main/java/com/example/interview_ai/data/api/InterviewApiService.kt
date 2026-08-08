package com.example.interview_ai.data.api

import com.example.interview_ai.data.model.EvaluationReport
import com.example.interview_ai.data.model.ParsedResume
import com.example.interview_ai.data.model.Question
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.Response

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

data class UserStats(
    val totalSessions: Int,
    val averageScore: Int,
    val totalHours: Float
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val targetRole: String,
    val parsedResume: ParsedResume? = null,
    val stats: UserStats? = null
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UpdateProfileRequest(
    val targetRole: String
)

data class ParseResumeRequest(
    val resumeText: String,
    val fileName: String
)

data class StartInterviewRequest(
    val targetRole: String,
    val difficulty: String,
    val category: String
)

data class StartInterviewResponse(
    val question: String
)

data class ConversationItem(
    val role: String,
    val text: String
)

data class NextQuestionRequest(
    val targetRole: String,
    val difficulty: String,
    val category: String,
    val conversationHistory: List<ConversationItem>,
    val currentAnswer: String
)

data class NextQuestionResponse(
    val nextQuestion: String,
    val isLastQuestion: Boolean
)

data class AnswerItem(
    val questionId: String,
    val questionText: String,
    val userAnswer: String
)

data class EvaluateRequest(
    val duration: String,
    val transcript: List<AnswerItem>,
    val role: String,
    val difficulty: String,
    val category: String
)

interface InterviewApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/auth/user")
    suspend fun getUser(): UserDto

    @POST("api/auth/logout")
    suspend fun logout(): okhttp3.ResponseBody

    @POST("api/auth/refresh")
    suspend fun refreshToken(): AuthResponse

    @PUT("api/user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UserDto

    @Multipart
    @POST("api/resume/parse")
    suspend fun parseResume(
        @Part file: MultipartBody.Part
    ): ParsedResume

    @PUT("api/resume/confirm")
    suspend fun confirmResume(): okhttp3.ResponseBody

    @DELETE("api/resume")
    suspend fun deleteResume(): okhttp3.ResponseBody

    @POST("api/interview/start")
    suspend fun startInterview(@Body request: StartInterviewRequest): StartInterviewResponse

    @POST("api/interview/next-question")
    suspend fun getNextQuestion(@Body request: NextQuestionRequest): NextQuestionResponse

    @POST("api/interview/evaluate")
    suspend fun evaluateInterview(@Body request: EvaluateRequest): EvaluationReport

    @GET("api/interview/history")
    suspend fun getHistory(): List<EvaluationReport>

    @GET("api/interview/report/{id}")
    suspend fun getReport(@Path("id") id: String): EvaluationReport

    @DELETE("api/interview/report/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Unit>
}
