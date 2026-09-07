package com.example.luxeval.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luxeval.data.repository.AuthRepository
import com.example.luxeval.data.repository.SessionRepository
import com.example.luxeval.presentation.appraisalsList.AppraisalsListScreen
import com.example.luxeval.presentation.appraisalsList.AppraisalsListViewModel
import com.example.luxeval.presentation.dashboard.DashboardScreen
import com.example.luxeval.presentation.dashboard.DashboardViewModel
import com.example.luxeval.presentation.search.SearchScreen
import com.example.luxeval.presentation.search.SearchViewModel
import com.example.luxeval.presentation.settings.SettingsScreen
import com.example.luxeval.presentation.settings.SettingsViewModel
import com.example.luxeval.presentation.util.BottomNavDestination
import com.example.luxeval.presentation.util.LuxeBottomNavBar
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Owns the bottom nav bar and its own inner NavHost for the four tab screens.
 * `outerNavController` is passed through for navigation that must leave this
 * shell entirely (e.g. opening an AppraisalDetail, which is full-screen with
 * no bottom bar).
 */
@Composable
fun MainScreen(outerNavController: NavHostController) {
    val innerNavController = rememberNavController()
    val backStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentTab = when (backStackEntry?.destination?.route) {
        AppraisalsList::class.qualifiedName -> BottomNavDestination.APPRAISALS
        Search::class.qualifiedName -> BottomNavDestination.SEARCH
        Settings::class.qualifiedName -> BottomNavDestination.SETTINGS
        else -> BottomNavDestination.HOME
    }

    Scaffold(
        bottomBar = {
            LuxeBottomNavBar(current = currentTab) { destination ->
                val route = when (destination) {
                    BottomNavDestination.HOME -> Dashboard
                    BottomNavDestination.APPRAISALS -> AppraisalsList
                    BottomNavDestination.SEARCH -> Search
                    BottomNavDestination.SETTINGS -> Settings
                }
                innerNavController.navigate(route) {
                    // Standard bottom-nav behavior: don't stack duplicate tab entries,
                    // and clear back up to the graph start so Back doesn't walk tab history.
                    popUpTo(innerNavController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) {
        NavHost(
            navController = innerNavController,
            modifier = Modifier.fillMaxSize().padding(bottom = 70.dp),
            startDestination = Dashboard,
        ) {
            composable<Dashboard> {
                val viewModel: DashboardViewModel = koinViewModel()
                DashboardScreen(
                    viewModel = viewModel,
                    onOpenAppraisal = { id -> outerNavController.navigate(AppraisalDetail(id)) },
                    onNavigateToBusinessProfile = { outerNavController.navigate(BusinessProfile) },
                )
            }
            composable<AppraisalsList> {
                val viewModel: AppraisalsListViewModel = koinViewModel()
                AppraisalsListScreen(
                    viewModel = viewModel,
                    onOpenAppraisal = { id -> outerNavController.navigate(AppraisalDetail(id)) },
                    onNavigateToBusinessProfile = { outerNavController.navigate(BusinessProfile) },
                )
            }
            composable<Settings> {
                val viewModel: SettingsViewModel = koinViewModel()
                val sessionRepository: SessionRepository = koinInject()
                val scope = rememberCoroutineScope()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToBusinessProfile = { outerNavController.navigate(BusinessProfile) },
                    onLogOut = { scope.launch { sessionRepository.signOutAndClearLocalData() } }
                )
            }
            composable<Search> {
                val viewModel: SearchViewModel = koinViewModel()
                SearchScreen(
                    viewModel = viewModel,
                    onOpenAppraisal = { id -> outerNavController.navigate(AppraisalDetail(id)) }
                )
            }
        }
    }
}