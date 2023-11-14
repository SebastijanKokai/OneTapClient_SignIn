package com.example.onetapsignin.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.onetapsignin.Screen
import com.example.onetapsignin.auth.AuthRepository
import com.example.onetapsignin.data.UserData

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val logoutState: LogoutState by viewModel.logoutState.collectAsState()
    val userState: UserData by viewModel.userState.collectAsState()

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
        CustomAsyncImage(context, userState.pictureUrl)
        CustomText(Modifier.padding(16.dp), value = "Display name: ${userState.displayName}")

        Row(
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            CustomText(value = "Name: ")
            CustomText(value = "${userState.firstName} ${userState.lastName}")
        }

        Button(onClick = {
            viewModel.signOut()
        }) {
            Text(text = "Sign out")
        }
    }
}

@Composable
fun CustomText(modifier: Modifier = Modifier, value: String?) {
    if (value.isNullOrEmpty()) {
        return
    }

    Text(
        modifier = modifier,
        text = value
    )
}

@Composable
fun CustomAsyncImage(context: Context, url: String?) {
    if (url.isNullOrEmpty()) {
        return
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = "Profile image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .size(64.dp)
            .clip(CircleShape)
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        rememberNavController(),
        viewModel(
            factory = HomeViewModelFactory(
                AuthRepository(
                    LocalContext.current
                )
            )
        ),
    )
}