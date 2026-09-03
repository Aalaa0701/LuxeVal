package com.example.luxeval.presentation.splash


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LocalDarkTheme
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.luxval_dark_theme_1__2_
import luxeval.shared.generated.resources.luxval_light_theme_1__2_
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

@Composable
fun SplashScreen() {
    val darkTheme = LocalDarkTheme.current
    val logo = if (darkTheme) Res.drawable.luxval_dark_theme_1__2_ else Res.drawable.luxval_light_theme_1__2_

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(logo),
                contentDescription = "LuxVal",
                modifier = Modifier.size(860.dp)
            )
        }
    }
}