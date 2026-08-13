package com.example.luxeval.presentation.settings


import com.example.luxeval.data.MeasurementUnit

data class SettingsUiState(
    val businessName: String = "",
    val licenseNumber: String = "",
    val businessAddress: String = "",
    val businessPhone: String = "",
    val logoPath: String? = null,
    val signaturePath: String? = null,
    val currencyCode: String = "USD",
    val measurementUnit: MeasurementUnit = MeasurementUnit.METRIC,
    val languageCode: String = "en",
    val isDarkModeEnabled: Boolean = false
)