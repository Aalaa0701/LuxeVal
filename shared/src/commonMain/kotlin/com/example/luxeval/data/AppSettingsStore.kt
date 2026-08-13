package com.example.luxeval.data

import com.russhwolf.settings.Settings

enum class MeasurementUnit { METRIC, IMPERIAL }

class AppSettingsStore(private val settings: Settings) {
    fun getCurrencyCode(): String = settings.getString("currency_code", "USD")
    fun setCurrencyCode(value: String) = settings.putString("currency_code", value)

    fun getMeasurementUnit(): MeasurementUnit =
        MeasurementUnit.valueOf(settings.getString("measurement_unit", MeasurementUnit.METRIC.name))
    fun setMeasurementUnit(value: MeasurementUnit) = settings.putString("measurement_unit", value.name)

    fun getLanguageCode(): String = settings.getString("language_code", "en")
    fun setLanguageCode(value: String) = settings.putString("language_code", value)

    fun isDarkModeEnabled(): Boolean = settings.getBoolean("dark_mode", false)
    fun setDarkModeEnabled(value: Boolean) = settings.putBoolean("dark_mode", value)
}