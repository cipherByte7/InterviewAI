package com.example.interview_ai.ui.navigation

sealed class Routes(val route: String) {

    object Splash : Routes("splash")

    object Login : Routes("login")

    object Register : Routes("register")

    object Dashboard : Routes("dashboard")

    object Interview : Routes("interview")

    object Report : Routes("report/{reportId}") {
        fun createRoute(reportId: String) = "report/$reportId"
    }

    object History : Routes("history")

    object Profile : Routes("profile")
}