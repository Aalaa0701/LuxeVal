package com.example.luxeval.i18n

object LanguageManager {
    val supportedLanguages = listOf("en", "ar", "es", "fr")
    const val DEFAULT_LANGUAGE = "en"

    /** Picks the best supported language for a given device/system locale tag,
     *  falling back to English if nothing matches. */
    fun resolve(deviceLanguageTag: String): String {
        val lang = deviceLanguageTag.substringBefore("-").lowercase()
        return if (lang in supportedLanguages) lang else DEFAULT_LANGUAGE
    }
}