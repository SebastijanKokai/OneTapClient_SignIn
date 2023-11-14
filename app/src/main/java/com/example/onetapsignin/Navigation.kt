package com.example.onetapsignin

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onetapsignin.auth.IAuthRepository
import com.example.onetapsignin.ui.home.HomeScreen
import com.example.onetapsignin.ui.login.LoginScreen
import com.example.onetapsignin.ui.login.LoginViewModel
import com.example.onetapsignin.ui.login.LoginViewModelFactory

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authRepository: IAuthRepository
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
            LoginScreen(navController, viewModel)
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