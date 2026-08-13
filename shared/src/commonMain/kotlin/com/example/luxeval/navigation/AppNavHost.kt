package com.example.luxeval.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailViewModel
import com.example.luxeval.presentation.appraisalsList.AppraisalsListViewModel
import com.example.luxeval.presentation.dashboard.DashboardScreen
import com.example.luxeval.presentation.dashboard.DashboardViewModel
import com.example.luxeval.presentation.search.SearchViewModel
import com.example.luxeval.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> { /* SplashScreen(navController) */ }
        composable<Login> { /* LoginScreen(navController) */ }
        composable<SignUp> { /* SignUpScreen(navController) */ }
        composable<ResetPassword> { /* ResetPasswordScreen(navController) */ }

        composable<Dashboard> {
            val viewModel: DashboardViewModel = koinViewModel()
            DashboardScreen(
                viewModel = viewModel,
                onOpenAppraisal = { id -> navController.navigate(AppraisalDetail(id)) }
            )
        /* DashboardScreen(navController) */
        }
        composable<AppraisalsList> {
            val viewModel: AppraisalsListViewModel = koinViewModel()
        /* AppraisalsListScreen(navController) */
        }
        composable<Search> {
            val viewModel: SearchViewModel = koinViewModel()
        /* SearchScreen(navController) */
        }
        composable<Settings> {
            val viewModel: SettingsViewModel = koinViewModel()
        /* SettingsScreen(navController) */
        }

        composable<AppraisalDetail> { backStackEntry ->
            val route: AppraisalDetail = backStackEntry.toRoute()
            val viewModel: AppraisalDetailViewModel = koinViewModel(
                parameters = { parametersOf(route.appraisalId) }
            )
            /* AppraisalDetailScreen(viewModel, navController) */
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