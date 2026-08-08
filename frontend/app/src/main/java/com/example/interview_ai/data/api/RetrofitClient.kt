package com.example.interview_ai.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 → host machine localhost from Android Emulator
    // Change to your machine's LAN IP when running on a real device
    private const val BASE_URL = "https://interviewai-nxn4.onrender.com"

    private var token: String? = null

    fun setToken(newToken: String?) {
        token = newToken
        // Rebuild apiService so the new token interceptor takes effect immediately
        _apiService = buildApiService()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Never retain or write the contents of uploaded resumes to logs.
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Time to establish TCP connection
            .readTimeout(120, TimeUnit.SECONDS)    // Time to read response (Gemini AI can take 20-60s)
            .writeTimeout(60, TimeUnit.SECONDS)    // Time to upload multipart PDF
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val requestBuilder = request.newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    private fun buildApiService(): InterviewApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InterviewApiService::class.java)
    }

    private var _apiService: InterviewApiService = buildApiService()

    val apiService: InterviewApiService
        get() = _apiService
}
