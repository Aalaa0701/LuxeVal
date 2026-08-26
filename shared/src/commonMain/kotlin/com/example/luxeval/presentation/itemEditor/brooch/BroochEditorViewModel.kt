// presentation/itemEditor/brooch/BroochEditorViewModel.kt
package com.example.luxeval.presentation.itemEditor.brooch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.photo.PhotoStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class BroochEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(BroochEditorUiState())
    val uiState: StateFlow<BroochEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existing = itemRepository.getBrooch(itemId)
            _uiState.value = if (existing != null) {
                existing.toUiState().copy(isLoading = false)
            } else {
                _uiState.value.copy(isLoading = false, isNewItem = true)
            }
        }
    }

    fun update(transform: (BroochEditorUiState) -> BroochEditorUiState) {
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
                val brooch = AppraisalItem.Brooch(
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
                    pinMechanism = state.pinMechanism,
                    heightMm = state.heightMm.toDoubleOrNull(),
                    widthMm = state.widthMm.toDoubleOrNull(),
                    motifDescription = state.motifDescription.ifBlank { null },
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
                itemRepository.saveItem(brooch)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.message)
            }
        }
    }
}

private fun AppraisalItem.Brooch.toUiState() = BroochEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
    conditionNotes = conditionNotes.orEmpty(),
    hallmarks = hallmarks.orEmpty(),
    grossWeightGrams = grossWeightGrams?.toString().orEmpty(),
    pinMechanism = pinMechanism,
    heightMm = heightMm?.toString().orEmpty(),
    widthMm = widthMm?.toString().orEmpty(),
    motifDescription = motifDescription.orEmpty(),
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