package com.example.erasmusapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.erasmusapp.screens.HomeScreen
import com.example.erasmusapp.screens.LoginScreen
import com.example.erasmusapp.screens.RegisterScreen
import com.example.erasmusapp.screens.SchoolListScreen
import com.example.erasmusapp.screens.SchoolDetailScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("school_list/{country}") { backStackEntry ->
            val country = backStackEntry.arguments?.getString("country") ?: ""
            SchoolListScreen(navController, country)
        }

        composable("school_detail/{schoolId}") { backStackEntry ->
            val schoolId = backStackEntry.arguments?.getString("schoolId") ?: ""
            SchoolDetailScreen(navController, schoolId)
        }
    }
}