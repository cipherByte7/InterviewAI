package com.example.interview_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.interview_ai.theme.InterviewAITheme
import com.example.interview_ai.ui.navigation.AppNavGraph
import com.example.interview_ai.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()

            InterviewAITheme(themeMode = themeMode) {

                val navController = rememberNavController()

                AppNavGraph(
                    navController = navController,
                    themeViewModel = themeViewModel
                )

            }

        }
    }
}
