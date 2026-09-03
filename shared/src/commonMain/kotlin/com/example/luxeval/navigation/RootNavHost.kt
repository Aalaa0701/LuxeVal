package com.example.luxeval.navigation

import androidx.compose.runtime.*
import com.example.luxeval.data.repository.AuthRepository
import com.example.luxeval.data.repository.AuthState
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.presentation.splash.SplashScreen
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun RootNavHost() {
    val authRepository: AuthRepository = koinInject()
    val authState by authRepository.authState.collectAsState()

    var minSplashElapsed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(700)
        minSplashElapsed = true
    }

    when (val state = authState) {
        is AuthState.Loading -> SplashScreen()
        is AuthState.LoggedOut -> if (minSplashElapsed) AuthNavHost() else SplashScreen()
        is AuthState.LoggedIn -> {
            if (!minSplashElapsed) {
                SplashScreen()
            } else {
                val syncRepository: SyncRepository = koinInject()
                LaunchedEffect(state.userId) { syncRepository.pullAll() }
                AppNavHost()
            }
        }
    }
}