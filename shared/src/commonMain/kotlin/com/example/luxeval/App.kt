package com.example.luxeval

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.luxeval.navigation.AppNavHost
import com.example.luxeval.navigation.RootNavHost
import com.example.luxeval.presentation.theme.LuxeValTheme
import com.example.luxeval.presentation.theme.ThemeController
import org.jetbrains.compose.resources.painterResource

import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.compose_multiplatform
import org.koin.compose.koinInject
import com.example.luxeval.presentation.util.initImageLoader

@Composable
@Preview
fun App(onReady: () -> Unit = {}) {
    val themeController: ThemeController = koinInject()
    val isDarkTheme by themeController.isDarkTheme.collectAsState()

    LuxeValTheme(darkTheme = isDarkTheme) {
        RootNavHost()
//        initImageLoader(context = )
    }
}