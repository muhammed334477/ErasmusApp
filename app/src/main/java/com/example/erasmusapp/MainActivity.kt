package com.example.erasmusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.erasmusapp.screens.*
import com.example.erasmusapp.ui.theme.ErasmusAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ErasmusAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

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

        composable("select_country") {
            CountryListScreen(navController)
        }

        composable("select_school") {
            SchoolListScreen(navController)
        }

        composable("select_school/{country}") { backStackEntry ->
            val selectedCountry = backStackEntry.arguments?.getString("country") ?: "Tümü"
            SchoolListScreen(navController, selectedCountry)
        }

        composable("school_detail/{schoolName}") { backStackEntry ->
            val schoolName = backStackEntry.arguments?.getString("schoolName") ?: "Bilinmeyen Okul"
            SchoolDetailScreen(navController, schoolName)
        }
    }
}