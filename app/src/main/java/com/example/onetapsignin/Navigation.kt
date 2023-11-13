package com.example.onetapsignin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onetapsignin.auth.IAuthRepository
import com.example.onetapsignin.ui.home.HomeScreen
import com.example.onetapsignin.ui.login.LoginScreen

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authRepository: IAuthRepository
) {

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(authRepository)
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Home : Screen("home_screen")
}