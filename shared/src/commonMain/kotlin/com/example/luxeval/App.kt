package com.example.luxeval

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.example.luxeval.data.ThemeMode
import com.example.luxeval.i18n.AppEnvironment
import com.example.luxeval.i18n.customAppLocale
import com.example.luxeval.navigation.AppNavHost
import com.example.luxeval.navigation.RootNavHost
import com.example.luxeval.presentation.settings.SettingsViewModel
import com.example.luxeval.presentation.theme.LuxeValTheme
import com.example.luxeval.presentation.theme.ThemeController
import org.jetbrains.compose.resources.painterResource

import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.compose_multiplatform
import org.koin.compose.koinInject
import com.example.luxeval.presentation.util.initImageLoader
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(onReady: () -> Unit = {}) {
    val themeController: ThemeController = koinInject()
    val isDarkTheme by themeController.isDarkTheme.collectAsState()

//    LuxeValTheme(darkTheme = isDarkTheme) {
//        RootNavHost()
////        initImageLoader(context = )
//    }
    val themeMode by themeController.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val languageCode = settingsState.languageCode
    customAppLocale = languageCode

    AppEnvironment {

        val isArabic = languageCode == "ar"

        LuxeValTheme(darkTheme = darkTheme) {

            CompositionLocalProvider(
                LocalLayoutDirection provides if (isArabic) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }
            ) {
                RootNavHost()
            }
        }
    }

//    LuxeValTheme(darkTheme = darkTheme) {
//        RootNavHost()
//    }
}