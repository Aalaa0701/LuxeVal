package com.example.luxeval.i18n


import com.example.luxeval.data.AppSettingsStore

class LanguageBootstrapper(private val settingsStore: AppSettingsStore) {
    /** Call once, as early as possible in app startup (before first Compose frame). */
    fun bootstrap() {
        val code = if (!settingsStore.hasLanguageBeenInitialized()) {
            // First launch: derive from device locale, supported set, else English
            val resolved = LanguageManager.resolve(LanguageApplier.currentDeviceLanguageTag())
            settingsStore.setLanguageCode(resolved)
            settingsStore.markLanguageInitialized()
            resolved
        } else {
            settingsStore.getLanguageCode() // respect the user's prior explicit choice
        }
        LanguageApplier.apply(code)
    }
}