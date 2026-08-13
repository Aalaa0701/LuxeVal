package com.example.luxeval.presentation.settings


import androidx.lifecycle.ViewModel
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.MeasurementUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val profileStore: AppraiserProfileStore,
    private val settingsStore: AppSettingsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadCurrentState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private fun loadCurrentState() = SettingsUiState(
        businessName = profileStore.getName(),
        licenseNumber = profileStore.getLicenseNumber(),
        businessAddress = profileStore.getBusinessAddress(),
        businessPhone = profileStore.getBusinessPhone(),
        logoPath = profileStore.getLogoPath(),
        signaturePath = profileStore.getSignaturePath(),
        currencyCode = settingsStore.getCurrencyCode(),
        measurementUnit = settingsStore.getMeasurementUnit(),
        languageCode = settingsStore.getLanguageCode(),
        isDarkModeEnabled = settingsStore.isDarkModeEnabled()
    )

    fun onBusinessNameChanged(value: String) {
        profileStore.setName(value)
        _uiState.value = _uiState.value.copy(businessName = value)
    }

    fun onLicenseNumberChanged(value: String) {
        profileStore.setLicenseNumber(value)
        _uiState.value = _uiState.value.copy(licenseNumber = value)
    }

    fun onBusinessAddressChanged(value: String) {
        profileStore.setBusinessAddress(value)
        _uiState.value = _uiState.value.copy(businessAddress = value)
    }

    fun onBusinessPhoneChanged(value: String) {
        profileStore.setBusinessPhone(value)
        _uiState.value = _uiState.value.copy(businessPhone = value)
    }

    fun onLogoPathChanged(path: String) {
        profileStore.setLogoPath(path)
        _uiState.value = _uiState.value.copy(logoPath = path)
    }

    fun onSignaturePathChanged(path: String) {
        profileStore.setSignaturePath(path)
        _uiState.value = _uiState.value.copy(signaturePath = path)
    }

    fun onCurrencyChanged(code: String) {
        settingsStore.setCurrencyCode(code)
        _uiState.value = _uiState.value.copy(currencyCode = code)
    }

    fun onMeasurementUnitChanged(unit: MeasurementUnit) {
        settingsStore.setMeasurementUnit(unit)
        _uiState.value = _uiState.value.copy(measurementUnit = unit)
    }

    fun onLanguageChanged(code: String) {
        settingsStore.setLanguageCode(code)
        _uiState.value = _uiState.value.copy(languageCode = code)
    }

    fun onDarkModeChanged(enabled: Boolean) {
        settingsStore.setDarkModeEnabled(enabled)
        _uiState.value = _uiState.value.copy(isDarkModeEnabled = enabled)
    }
}