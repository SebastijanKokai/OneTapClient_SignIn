package com.example.onetapsignin.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.onetapsignin.Screen
import com.example.onetapsignin.auth.AuthRepository

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val logoutState: LogoutState by viewModel.logoutState.collectAsState()

    LaunchedEffect(key1 = logoutState) {
        if (logoutState.isSuccessful) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
        } else if (logoutState.errorMessage.isNotEmpty()) {
            Toast.makeText(context, logoutState.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        rememberNavController(), viewModel(
            factory = HomeViewModelFactory(
                AuthRepository(
                    LocalContext.current
                )
            )
        )
    )
}