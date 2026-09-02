package com.example.luxeval.presentation.itemEditor.pearl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.itemEditor.common.PearlFormState
import com.example.luxeval.presentation.photo.PhotoStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class PearlEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage,
    private val pricingRepository: PricingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PearlEditorUiState())
    val uiState: StateFlow<PearlEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existing = itemRepository.getPearlJewelry(itemId)
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
            }.onFailure {
                _uiState.value = _uiState.value.copy(isFetchingPrice = false, saveError = "Couldn't fetch market price")
            }
        }
    }

    fun update(transform: (PearlEditorUiState) -> PearlEditorUiState) {
        _uiState.value = transform(_uiState.value)
    }
    fun removePhoto(uri: String) {
        _uiState.value = _uiState.value.copy(photoUris = _uiState.value.photoUris - uri)
        viewModelScope.launch { photoStorage.deleteImage(uri) }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, saveError = null)
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val pearl = AppraisalItem.PearlJewelry(
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
                    pearlType = state.pearl.pearlType,
                    sizeMm = state.pearl.sizeMm.toDoubleOrNull(),
                    shape = state.pearl.shape,
                    luster = state.pearl.luster,
                    surfaceQuality = state.pearl.surfaceQuality,
                    overtoneColor = state.pearl.overtoneColor.ifBlank { null },
                    pearlCount = state.pearl.pearlCount.toIntOrNull() ?: 1,
                    isMatchedStrand = state.pearl.isMatchedStrand,
                    metal = state.metal.takeIf { state.hasClaspMetal }?.let {
                        MetalDetails(
                            metalType = it.metalType,
                            purity = it.purity,
                            color = it.color,
                            netWeightGrams = it.netWeightGrams.toDoubleOrNull(),
                            isPlated = it.isPlated,
                            platingBaseMetal = it.platingBaseMetal.ifBlank { null }
                        )
                    }
                )
                itemRepository.saveItem(pearl)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.message)
            }
        }
    }
}

private fun AppraisalItem.PearlJewelry.toUiState() = PearlEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
    conditionNotes = conditionNotes.orEmpty(),
    hallmarks = hallmarks.orEmpty(),
    grossWeightGrams = grossWeightGrams?.toString().orEmpty(),
    pearl = PearlFormState(
        pearlType = pearlType,
        sizeMm = sizeMm?.toString().orEmpty(),
        shape = shape,
        luster = luster,
        surfaceQuality = surfaceQuality,
        overtoneColor = overtoneColor.orEmpty(),
        pearlCount = pearlCount.toString(),
        isMatchedStrand = isMatchedStrand
    ),
    hasClaspMetal = metal != null,
    metal = metal?.let {
        MetalFormState(
            metalType = it.metalType, purity = it.purity, color = it.color,
            netWeightGrams = it.netWeightGrams?.toString().orEmpty(),
            isPlated = it.isPlated, platingBaseMetal = it.platingBaseMetal.orEmpty()
        )
    } ?: MetalFormState(),
    estimatedValue = estimatedValue?.toString().orEmpty(),
    notes = notes.orEmpty()
)