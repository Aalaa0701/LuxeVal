package com.example.luxeval.presentation.theme


import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Bridges the synchronous AppSettingsStore (multiplatform-settings, no Flow support)
 * with reactive Compose state. Both SettingsViewModel and App() read/write through
 * this single instance so a toggle in Settings is reflected at the theme root immediately.
 */
class ThemeController(private val settingsStore: AppSettingsStore) {
    private val _themeMode = MutableStateFlow(settingsStore.getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        settingsStore.setThemeMode(mode)
        _themeMode.value = mode
    }
    private val _isDarkTheme = MutableStateFlow(settingsStore.isDarkModeEnabled())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        settingsStore.setDarkModeEnabled(enabled)
        _isDarkTheme.value = enabled
    }
}