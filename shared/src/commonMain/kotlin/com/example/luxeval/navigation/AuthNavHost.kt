package com.example.luxeval.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.presentation.auth.AuthScreen
import com.example.luxeval.presentation.auth.AuthViewModel
import com.example.luxeval.presentation.auth.ResetPasswordScreen
import com.example.luxeval.presentation.onboarding.OnboardingScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthNavHost() {
    val settingsStore: AppSettingsStore = koinInject()
    val navController = rememberNavController()
    val start = if (settingsStore.hasCompletedOnboarding()) Auth else Onboarding

    NavHost(navController = navController, startDestination = start) {
        composable<Onboarding> {
            OnboardingScreen(onFinished = { navController.navigate(Auth) { popUpTo(Onboarding) { inclusive = true } } })
        }
        composable<Auth> {
            val viewModel: AuthViewModel = koinViewModel()
            AuthScreen(
                viewModel = viewModel,
                onNavigateToResetPassword = { navController.navigate(ResetPassword) }
            )
        }
        composable<ResetPassword> {
            val viewModel: AuthViewModel = koinViewModel() // shares no state with Auth's instance — fine, it's stateless besides email
            ResetPasswordScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}