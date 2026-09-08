package com.example.luxeval.i18n

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

actual object LanguageApplier {
    actual fun currentDeviceLanguageTag(): String =
        Locale.getDefault().language

    actual fun apply(languageCode: String) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}