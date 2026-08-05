package com.example.interview_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.interview_ai.theme.InterviewAITheme
import com.example.interview_ai.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            InterviewAITheme {

                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController
                )

            }

        }
    }
}