package com.example.interview_ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interview_ai.ui.screens.auth.LoginScreen
import com.example.interview_ai.ui.screens.auth.RegisterScreen
import com.example.interview_ai.ui.screens.dashboard.DashboardScreen
import com.example.interview_ai.ui.screens.history.HistoryScreen
import com.example.interview_ai.ui.screens.interview.InterviewScreen
import com.example.interview_ai.ui.screens.profile.ProfileScreen
import com.example.interview_ai.ui.screens.report.ReportScreen
import com.example.interview_ai.ui.screens.splash.SplashScreen
import com.example.interview_ai.viewmodel.AuthViewModel
import com.example.interview_ai.viewmodel.DashboardViewModel
import com.example.interview_ai.viewmodel.InterviewViewModel
import com.example.interview_ai.viewmodel.ReportViewModel
import com.example.interview_ai.viewmodel.HistoryViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel(),
    interviewViewModel: InterviewViewModel = viewModel(),
    reportViewModel: ReportViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {

        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Routes.Dashboard.route) {
            DashboardScreen(navController, dashboardViewModel)
        }

        composable(Routes.Interview.route) {
            InterviewScreen(navController, interviewViewModel, dashboardViewModel)
        }

        composable(Routes.Report.route) {
            ReportScreen(navController, reportViewModel)
        }

        composable(Routes.History.route) {
            HistoryScreen(navController, historyViewModel)
        }

        composable(Routes.Profile.route) {
            ProfileScreen(navController)
        }
    }
}