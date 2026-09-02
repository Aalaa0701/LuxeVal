package com.example.luxeval.presentation.itemEditor.necklace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.photo.PhotoStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class NecklaceEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage,
    private val pricingRepository: PricingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NecklaceEditorUiState())
    val uiState: StateFlow<NecklaceEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existing = itemRepository.getNecklace(itemId)
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

    fun update(transform: (NecklaceEditorUiState) -> NecklaceEditorUiState) {
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
                val necklace = AppraisalItem.Necklace(
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
                    lengthCm = state.lengthCm.toDoubleOrNull(),
                    chainStyle = state.chainStyle.ifBlank { null },
                    claspType = state.claspType,
                    hasPendantAttached = state.hasPendantAttached,
                    metal = MetalDetails(
                        metalType = state.metal.metalType,
                        purity = state.metal.purity,
                        color = state.metal.color,
                        netWeightGrams = state.metal.netWeightGrams.toDoubleOrNull(),
                        isPlated = state.metal.isPlated,
                        platingBaseMetal = state.metal.platingBaseMetal.ifBlank { null }
                    ),
                    gemstone = state.gemstone.takeIf { it.enabled }?.let {
                        GemstoneDetails(
                            stoneType = it.stoneType,
                            originType = it.originType,
                            caratWeight = it.caratWeight.toDoubleOrNull() ?: 0.0,
                            shape = it.shape,
                            colorGrade = it.colorGrade.ifBlank { null },
                            clarityGrade = it.clarityGrade.ifBlank { null },
                            treatment = it.treatment,
                            fluorescence = it.fluorescence,
                            settingType = it.settingType,
                            stoneCount = it.stoneCount.toIntOrNull() ?: 1,
                            certificationLab = it.certificationLab.ifBlank { null },
                            certificationNumber = it.certificationNumber.ifBlank { null },
                            measurementsMm = it.measurementsMm.ifBlank { null }
                        )
                    }
                )
                itemRepository.saveItem(necklace)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.message)
            }
        }
    }
}

private fun AppraisalItem.Necklace.toUiState() = NecklaceEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
    conditionNotes = conditionNotes.orEmpty(),
    hallmarks = hallmarks.orEmpty(),
    grossWeightGrams = grossWeightGrams?.toString().orEmpty(),
    lengthCm = lengthCm?.toString().orEmpty(),
    chainStyle = chainStyle.orEmpty(),
    claspType = claspType,
    hasPendantAttached = hasPendantAttached,
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