package com.example.luxeval.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailScreen
import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = MainGraph) {
        composable<Splash> { /* SplashScreen(navController) */ }
        composable<Login> { /* LoginScreen(navController) */ }
        composable<SignUp> { /* SignUpScreen(navController) */ }
        composable<ResetPassword> { /* ResetPasswordScreen(navController) */ }

        composable<MainGraph> {
            MainScreen(outerNavController = navController)
        }

        composable<AppraisalDetail> { backStackEntry ->
            val route: AppraisalDetail = backStackEntry.toRoute()
            val viewModel: AppraisalDetailViewModel = koinViewModel(
                parameters = { parametersOf(route.appraisalId) }
            )
            AppraisalDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddItem = { category ->
                    navController.navigate(ItemEditor(appraisalId = route.appraisalId, category = category.name))
                },
                onPreviewReport = { navController.navigate(ReportPreview(route.appraisalId)) }
            )
        }
        composable<ItemEditor> { backStackEntry ->
            val route: ItemEditor = backStackEntry.toRoute()
            /* dispatch on route.category to the right editor composable */
        }
        composable<ReportPreview> { backStackEntry ->
            val route: ReportPreview = backStackEntry.toRoute()
            /* ReportPreviewScreen(route.appraisalId) */
        }
    }
}