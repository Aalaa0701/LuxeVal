package com.example.luxeval.presentation.settings


import com.example.luxeval.data.MeasurementUnit
import com.example.luxeval.data.ThemeMode
import com.example.luxeval.presentation.util.UserError

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
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isUploadingLogo: Boolean = false,
    val isUploadingSignature: Boolean = false,
    val uploadError: UserError? = null
)