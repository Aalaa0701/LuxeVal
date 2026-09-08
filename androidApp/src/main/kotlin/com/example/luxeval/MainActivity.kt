package com.example.luxeval

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.luxeval.i18n.LanguageBootstrapper
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val languageBootstrapper: LanguageBootstrapper by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        languageBootstrapper.bootstrap()
        setContent { App() }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}