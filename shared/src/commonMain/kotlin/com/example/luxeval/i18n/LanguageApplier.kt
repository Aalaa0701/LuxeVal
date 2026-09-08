package com.example.luxeval.i18n

expect object LanguageApplier {
    fun currentDeviceLanguageTag(): String
    fun apply(languageCode: String)
}