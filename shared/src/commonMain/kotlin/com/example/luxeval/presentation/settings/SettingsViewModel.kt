package com.example.luxeval.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.MeasurementUnit
import com.example.luxeval.data.ThemeMode
import com.example.luxeval.data.repository.BusinessProfileRepository
import com.example.luxeval.presentation.theme.ThemeController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SettingsViewModel(
    private val profileStore: AppraiserProfileStore,
    private val settingsStore: AppSettingsStore,
    private val themeController: ThemeController,
    private val businessProfileRepository: BusinessProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(loadCurrentState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // Fired on every text-field/currency/unit change. Debounced so typing a
    // business name doesn't push a network request per keystroke — only
    // pushes once 800ms have passed since the last edit.
    private val pushTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        pushTrigger
            .debounce(800)
            .onEach { businessProfileRepository.pushFieldsAsync() }
            .launchIn(viewModelScope)
    }

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
        themeMode = settingsStore.getThemeMode()
    )

    private fun schedulePush() {
        pushTrigger.tryEmit(Unit)
    }

    fun onBusinessNameChanged(value: String) {
        profileStore.setName(value)
        _uiState.value = _uiState.value.copy(businessName = value)
        schedulePush()
    }

    fun onLicenseNumberChanged(value: String) {
        profileStore.setLicenseNumber(value)
        _uiState.value = _uiState.value.copy(licenseNumber = value)
        schedulePush()
    }

    fun onBusinessAddressChanged(value: String) {
        profileStore.setBusinessAddress(value)
        _uiState.value = _uiState.value.copy(businessAddress = value)
        schedulePush()
    }

    fun onBusinessPhoneChanged(value: String) {
        profileStore.setBusinessPhone(value)
        _uiState.value = _uiState.value.copy(businessPhone = value)
        schedulePush()
    }

    /** Replaces the old synchronous setLogoPath — now uploads to Supabase
     *  Storage and pushes the profile row. The local path is shown
     *  immediately (optimistic) while the upload runs in the background;
     *  once it succeeds, the state is updated to the durable supabase://
     *  key so a later PDF export / cross-device view can resolve it. */
    fun onLogoPathChanged(localPath: String) {
        profileStore.setLogoPath(localPath)
        _uiState.value = _uiState.value.copy(
            logoPath = localPath,
            isUploadingLogo = true,
            uploadError = null
        )
        viewModelScope.launch {
            try {
                val remoteUri = businessProfileRepository.uploadLogo(localPath)
                _uiState.value = _uiState.value.copy(logoPath = remoteUri, isUploadingLogo = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingLogo = false,
                    uploadError = e.message ?: "Couldn't upload logo"
                )
            }
        }
    }

    fun onSignaturePathChanged(localPath: String) {
        profileStore.setSignaturePath(localPath)
        _uiState.value = _uiState.value.copy(
            signaturePath = localPath,
            isUploadingSignature = true,
            uploadError = null
        )
        viewModelScope.launch {
            try {
                val remoteUri = businessProfileRepository.uploadSignature(localPath)
                _uiState.value = _uiState.value.copy(signaturePath = remoteUri, isUploadingSignature = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingSignature = false,
                    uploadError = e.message ?: "Couldn't upload signature"
                )
            }
        }
    }

    fun onCurrencyChanged(code: String) {
        settingsStore.setCurrencyCode(code)
        _uiState.value = _uiState.value.copy(currencyCode = code)
        schedulePush()
    }

    fun onMeasurementUnitChanged(unit: MeasurementUnit) {
        settingsStore.setMeasurementUnit(unit)
        _uiState.value = _uiState.value.copy(measurementUnit = unit)
        schedulePush()
    }

    fun onLanguageChanged(code: String) {
        settingsStore.setLanguageCode(code)
        _uiState.value = _uiState.value.copy(languageCode = code)
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        themeController.setThemeMode(mode)
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun dismissUploadError() {
        _uiState.value = _uiState.value.copy(uploadError = null)
    }
}