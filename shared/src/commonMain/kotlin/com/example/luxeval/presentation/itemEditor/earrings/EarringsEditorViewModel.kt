// presentation/itemEditor/earrings/EarringsEditorViewModel.kt
package com.example.luxeval.presentation.itemEditor.earrings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PhotoRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import com.example.luxeval.presentation.photo.PhotoStorage
import com.example.luxeval.presentation.util.ErrorContext
import com.example.luxeval.presentation.util.toUserError

class EarringsEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage,
    private val pricingRepository: PricingRepository,
    private val syncRepository: SyncRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EarringsEditorUiState())
    val uiState: StateFlow<EarringsEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existing = itemRepository.getEarrings(itemId)
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
            pricingRepository.estimateMetalValue(
                state.metal.metalType, state.metal.purity, state.metal.netWeightGrams.toDoubleOrNull() ?: 0.0
            ).onSuccess { value ->
                _uiState.value = _uiState.value.copy(
                    estimatedValue = value.toString(),
                    valueSource = ValueSource.API_FETCHED,
                    isFetchingPrice = false
                )
            }.onFailure { e->
                _uiState.value = _uiState.value.copy(isFetchingPrice = false, saveError = e.toUserError(
                    ErrorContext.PRICE_FETCH))
            }
        }
    }
    fun dismissSaveError() {
        _uiState.value = _uiState.value.copy(saveError = null)
    }

    fun update(transform: (EarringsEditorUiState) -> EarringsEditorUiState) {
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
                val earrings = AppraisalItem.Earrings(
                    id = itemId,
                    itemName = state.itemName,
                    appraisalId = appraisalId,
                    photoUris = state.photoUris,
                    condition = state.condition,
                    conditionNotes = state.conditionNotes.ifBlank { null },
                    grossWeightGrams = state.grossWeightGrams.toDoubleOrNull(),
                    hallmarks = state.hallmarks.ifBlank { null },
                    estimatedValue = state.estimatedValue.toDoubleOrNull(),
                    currency = "USD",
                    valuationBasis = ValuationBasis.RETAIL_REPLACEMENT,
                    valueSource = ValueSource.MANUAL,
                    notes = state.notes.ifBlank { null },
                    createdAt = now,
                    updatedAt = now,
                    style = state.style,
                    backing = state.backing,
                    isConfirmedPair = state.isConfirmedPair,
                    hoopDiameterMm = state.hoopDiameterMm.toDoubleOrNull(),
                    metal = MetalDetails(
                        metalType = state.metal.metalType, purity = state.metal.purity, color = state.metal.color,
                        netWeightGrams = state.metal.netWeightGrams.toDoubleOrNull(),
                        isPlated = state.metal.isPlated, platingBaseMetal = state.metal.platingBaseMetal.ifBlank { null }
                    ),
                    gemstone = state.gemstone.takeIf { it.enabled }?.let {
                        GemstoneDetails(
                            stoneType = it.stoneType, originType = it.originType,
                            caratWeight = it.caratWeight.toDoubleOrNull() ?: 0.0, shape = it.shape,
                            colorGrade = it.colorGrade.ifBlank { null }, clarityGrade = it.clarityGrade.ifBlank { null },
                            treatment = it.treatment, fluorescence = it.fluorescence, settingType = it.settingType,
                            stoneCount = it.stoneCount.toIntOrNull() ?: 1,
                            certificationLab = it.certificationLab.ifBlank { null },
                            certificationNumber = it.certificationNumber.ifBlank { null },
                            measurementsMm = it.measurementsMm.ifBlank { null }
                        )
                    }
                )
                val savedEarring = itemRepository.saveItem(earrings)
                syncRepository.pushItemAsync(savedEarring)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.toUserError(ErrorContext.SAVE))
            }
        }
    }
}

private fun AppraisalItem.Earrings.toUiState() = EarringsEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
    conditionNotes = conditionNotes.orEmpty(),
    hallmarks = hallmarks.orEmpty(),
    grossWeightGrams = grossWeightGrams?.toString().orEmpty(),
    style = style,
    backing = backing,
    isConfirmedPair = isConfirmedPair,
    hoopDiameterMm = hoopDiameterMm?.toString().orEmpty(),
    metal = MetalFormState(
        metalType = metal.metalType, purity = metal.purity, color = metal.color,
        netWeightGrams = metal.netWeightGrams?.toString().orEmpty(),
        isPlated = metal.isPlated, platingBaseMetal = metal.platingBaseMetal.orEmpty()
    ),
    gemstone = gemstone?.let {
        GemstoneFormState(
            enabled = true, stoneType = it.stoneType, originType = it.originType,
            caratWeight = it.caratWeight.toString(), shape = it.shape,
            colorGrade = it.colorGrade.orEmpty(), clarityGrade = it.clarityGrade.orEmpty(),
            treatment = it.treatment, fluorescence = it.fluorescence, settingType = it.settingType,
            stoneCount = it.stoneCount.toString(), certificationLab = it.certificationLab.orEmpty(),
            certificationNumber = it.certificationNumber.orEmpty(), measurementsMm = it.measurementsMm.orEmpty()
        )
    } ?: GemstoneFormState(),
    estimatedValue = estimatedValue?.toString().orEmpty(),
    notes = notes.orEmpty()
)