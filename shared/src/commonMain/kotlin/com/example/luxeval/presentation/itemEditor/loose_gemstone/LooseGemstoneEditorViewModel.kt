package com.example.luxeval.presentation.itemEditor.loose_gemstone


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PhotoRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.photo.PhotoStorage
import com.example.luxeval.presentation.util.ErrorContext
import com.example.luxeval.presentation.util.toUserError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class LooseGemstoneEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage,
    private val pricingRepository: PricingRepository,
    private val syncRepository: SyncRepository,
    private val photoRepository: PhotoRepository,
    private val settingsStore: AppSettingsStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LooseGemstoneEditorUiState(currencyCode = settingsStore.getCurrencyCode(), measurementUnit = settingsStore.getMeasurementUnit()))
    val uiState: StateFlow<LooseGemstoneEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existing = itemRepository.getLooseGemstone(itemId)
            _uiState.value = if (existing != null) {
                existing.toUiState().copy(isLoading = false)
            } else {
                _uiState.value.copy(isLoading = false, isNewItem = true)
            }
        }
    }
    fun onEstimatedValueChanged(v: String) {
        _uiState.value = _uiState.value.copy(estimatedValue = v, valueSource = ValueSource.MANUAL)
    }
    fun removePhoto(uri: String) {
        _uiState.value = _uiState.value.copy(photoUris = _uiState.value.photoUris - uri)
        viewModelScope.launch {
            if (uri.startsWith("supabase://")) photoRepository.deletePhoto(uri)
            else photoStorage.deleteImage(uri)
        }
    }
    fun fetchMarketPrice() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isFetchingPrice = true)
//            pricingRepository.estimateMetalValue(
//                state.metal.metalType, state.metal.purity, state.metal.netWeightGrams.toDoubleOrNull() ?: 0.0
//            ).onSuccess { value ->
//                _uiState.value = _uiState.value.copy(
//                    estimatedValue = value.toString(),
//                    valueSource = ValueSource.API_FETCHED,
//                    isFetchingPrice = false
//                )
//            }.onFailure {
//                _uiState.value = _uiState.value.copy(isFetchingPrice = false, saveError = "Couldn't fetch market price")
//            }
        }
    }

    fun update(transform: (LooseGemstoneEditorUiState) -> LooseGemstoneEditorUiState) {
        _uiState.value = transform(_uiState.value)
    }
//    fun removePhoto(uri: String) {
//        _uiState.value = _uiState.value.copy(photoUris = _uiState.value.photoUris - uri)
//        viewModelScope.launch { photoStorage.deleteImage(uri) }
//    }
    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, saveError = null)
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val g = state.gemstone
                val item = AppraisalItem.LooseGemstone(
                    id = itemId,
                    itemName = state.itemName,
                    appraisalId = appraisalId,
                    photoUris = state.photoUris,
                    condition = state.condition,
                    conditionNotes = state.conditionNotes.ifBlank { null },
                    grossWeightGrams = state.grossWeightGrams.toDoubleOrNull(),
                    hallmarks = null,
                    estimatedValue = state.estimatedValue.toDoubleOrNull(),
                    currency = state.currencyCode,
                    measurementUnit = state.measurementUnit,
                    valuationBasis = ValuationBasis.RETAIL_REPLACEMENT,
                    valueSource = ValueSource.MANUAL,
                    notes = state.notes.ifBlank { null },
                    createdAt = now,
                    updatedAt = now,
                    gemstone = GemstoneDetails(
                        stoneType = g.stoneType,
                        originType = g.originType,
                        caratWeight = g.caratWeight.toDoubleOrNull() ?: 0.0,
                        shape = g.shape,
                        colorGrade = g.colorGrade.ifBlank { null },
                        clarityGrade = g.clarityGrade.ifBlank { null },
                        treatment = g.treatment,
                        fluorescence = g.fluorescence,
                        settingType = g.settingType,
                        stoneCount = g.stoneCount.toIntOrNull() ?: 1,
                        certificationLab = g.certificationLab.ifBlank { null },
                        certificationNumber = g.certificationNumber.ifBlank { null },
                        measurementsMm = g.measurementsMm.ifBlank { null }
                    )
                )
                val savedItem = itemRepository.saveItem(item)
                syncRepository.pushItemAsync(savedItem)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.toUserError(
                    ErrorContext.SAVE))
            }
        }
    }
    fun dismissSaveError() {
        _uiState.value = _uiState.value.copy(saveError = null)
    }
}

private fun AppraisalItem.LooseGemstone.toUiState() = LooseGemstoneEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
    currencyCode = currency,
    measurementUnit = measurementUnit,
    conditionNotes = conditionNotes.orEmpty(),
    grossWeightGrams = grossWeightGrams?.toString().orEmpty(),
    gemstone = GemstoneFormState(
        enabled = true, stoneType = gemstone.stoneType, originType = gemstone.originType,
        caratWeight = gemstone.caratWeight.toString(), shape = gemstone.shape,
        colorGrade = gemstone.colorGrade.orEmpty(), clarityGrade = gemstone.clarityGrade.orEmpty(),
        treatment = gemstone.treatment, fluorescence = gemstone.fluorescence, settingType = gemstone.settingType,
        stoneCount = gemstone.stoneCount.toString(), certificationLab = gemstone.certificationLab.orEmpty(),
        certificationNumber = gemstone.certificationNumber.orEmpty(), measurementsMm = gemstone.measurementsMm.orEmpty()
    ),
    estimatedValue = estimatedValue?.toString().orEmpty(),
    notes = notes.orEmpty()
)