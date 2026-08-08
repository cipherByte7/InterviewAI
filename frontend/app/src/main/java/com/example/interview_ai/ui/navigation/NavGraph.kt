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
import com.example.interview_ai.viewmodel.ThemeViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel(),
    interviewViewModel: InterviewViewModel = viewModel(),
    reportViewModel: ReportViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        enterTransition = {
            androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300))
        },
        exitTransition = {
            androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
        },
        popEnterTransition = {
            androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300))
        },
        popExitTransition = {
            androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing)
            ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
        }
    ) {

        composable(
            route = Routes.Splash.route,
            enterTransition = { androidx.compose.animation.EnterTransition.None },
            exitTransition = { androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(400)) }
        ) {
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

        composable(
            route = Routes.Report.route,
            arguments = listOf(
                navArgument("reportId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportScreen(
                navController = navController,
                viewModel = reportViewModel,
                interviewViewModel = interviewViewModel,
                reportId = reportId
            )
        }

        composable(Routes.History.route) {
            HistoryScreen(navController, historyViewModel)
        }

        composable(Routes.Profile.route) {
            ProfileScreen(navController, authViewModel, dashboardViewModel, themeViewModel)
        }
    }
}
