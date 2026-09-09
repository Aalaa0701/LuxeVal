package com.example.luxeval.i18n

import java.util.Locale

actual object LanguageApplier {

    actual fun currentDeviceLanguageTag(): String =
        Locale.getDefault().language

    actual fun apply(languageCode: String) {
        Locale.setDefault(Locale.forLanguageTag(languageCode))
    }
}