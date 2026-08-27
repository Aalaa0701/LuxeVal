package com.example.luxeval.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.luxeval.domain.ItemCategory
import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailScreen
import com.example.luxeval.presentation.appraisalDetail.AppraisalDetailViewModel
import com.example.luxeval.presentation.itemEditor.bracelet.BraceletEditorScreen
import com.example.luxeval.presentation.itemEditor.bracelet.BraceletEditorViewModel
import com.example.luxeval.presentation.itemEditor.brooch.BroochEditorScreen
import com.example.luxeval.presentation.itemEditor.brooch.BroochEditorViewModel
import com.example.luxeval.presentation.itemEditor.earrings.EarringsEditorScreen
import com.example.luxeval.presentation.itemEditor.earrings.EarringsEditorViewModel
import com.example.luxeval.presentation.itemEditor.loose_gemstone.LooseGemstoneEditorScreen
import com.example.luxeval.presentation.itemEditor.loose_gemstone.LooseGemstoneEditorViewModel
import com.example.luxeval.presentation.itemEditor.necklace.NecklaceEditorScreen
import com.example.luxeval.presentation.itemEditor.necklace.NecklaceEditorViewModel
import com.example.luxeval.presentation.itemEditor.pearl.PearlEditorScreen
import com.example.luxeval.presentation.itemEditor.pearl.PearlEditorViewModel
import com.example.luxeval.presentation.itemEditor.pendant.PendantEditorScreen
import com.example.luxeval.presentation.itemEditor.pendant.PendantEditorViewModel
import com.example.luxeval.presentation.itemEditor.ring.RingEditorScreen
import com.example.luxeval.presentation.itemEditor.ring.RingEditorViewModel
import com.example.luxeval.presentation.itemEditor.watch.WatchEditorScreen
import com.example.luxeval.presentation.itemEditor.watch.WatchEditorViewModel
import com.example.luxeval.presentation.report.ReportPreviewScreen
import com.example.luxeval.presentation.report.ReportPreviewViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

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
                key = "appraisal-detail-${route.appraisalId}",
                parameters = { parametersOf(route.appraisalId) }
            )
            AppraisalDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddItem = { category ->
                    navController.navigate(ItemEditor(appraisalId = route.appraisalId, category = category.name, itemId = Uuid.random().toString()))
                },
                onPreviewReport = { navController.navigate(ReportPreview(route.appraisalId)) }
            )
        }
        composable<ItemEditor> { backStackEntry ->
            val route: ItemEditor = backStackEntry.toRoute()
            val onBack: () -> Unit = {
                navController.popBackStack()
            }

            val onSaved: () -> Unit = {
                navController.popBackStack()
            }

            when (ItemCategory.valueOf(route.category)) {
                ItemCategory.RING -> {
                    val viewModel: RingEditorViewModel = koinViewModel(
                        key = "ring-editor-${route.itemId ?: backStackEntry.id}",
                        parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    RingEditorScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() }
                    )
                }
                ItemCategory.NECKLACE -> {
                    val viewModel: NecklaceEditorViewModel = koinViewModel(
                        key = "necklace-editor-${route.itemId}",
                        parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    NecklaceEditorScreen(
                        viewModel,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() }
                    )
                }
                ItemCategory.WATCH -> {
                    val viewModel: WatchEditorViewModel = koinViewModel(
                        key = "watch-editor-${route.itemId}",
                        parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    WatchEditorScreen(viewModel, onBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
                }
                ItemCategory.PEARL_JEWELRY -> {
                    val viewModel: PearlEditorViewModel = koinViewModel(
                        key = "pearl-editor-${route.itemId}",
                        parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    PearlEditorScreen(viewModel, onBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
                }
                ItemCategory.LOOSE_GEMSTONE -> {
                    val vm: LooseGemstoneEditorViewModel = koinViewModel(
                        key = "loosegem-editor-${route.itemId}", parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    LooseGemstoneEditorScreen(vm, onBack, onSaved)
                }
                ItemCategory.PENDANT -> {
                    val vm: PendantEditorViewModel = koinViewModel(
                        key = "pendant-editor-${route.itemId}", parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    PendantEditorScreen(vm, onBack, onSaved)
                }
                ItemCategory.BRACELET -> {
                    val vm: BraceletEditorViewModel = koinViewModel(
                        key = "bracelet-editor-${route.itemId}", parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    BraceletEditorScreen(vm, onBack, onSaved)
                }
                ItemCategory.EARRINGS -> {
                    val vm: EarringsEditorViewModel = koinViewModel(
                        key = "earrings-editor-${route.itemId}", parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    EarringsEditorScreen(vm, onBack, onSaved)
                }
                ItemCategory.BROOCH -> {
                    val vm: BroochEditorViewModel = koinViewModel(
                        key = "brooch-editor-${route.itemId}", parameters = { parametersOf(route.appraisalId, route.itemId) }
                    )
                    BroochEditorScreen(vm, onBack, onSaved)
                }
                ItemCategory.OTHER -> { /* CategoryPickerSheet already filters this out — unreachable */ }
            }
        }
        // navigation/AppNavHost.kt
        composable<ReportPreview> { backStackEntry ->
            val route: ReportPreview = backStackEntry.toRoute()
            val viewModel: ReportPreviewViewModel = koinViewModel(
                key = "report-preview-${route.appraisalId}",
                parameters = { parametersOf(route.appraisalId) }
            )
            ReportPreviewScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onExportPdf = {
                    viewModel.exportPdf(
                        onExported = { path -> /* share sheet / open file — platform share intent, next step if wanted */ },
                        onError = { /* show snackbar */ }
                    )
                }
            )
        }
    }
}