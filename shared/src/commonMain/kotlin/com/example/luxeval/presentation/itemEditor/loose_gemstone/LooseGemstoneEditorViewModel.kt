package com.example.luxeval.presentation.itemEditor.loose_gemstone


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.photo.PhotoStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class LooseGemstoneEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(LooseGemstoneEditorUiState())
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

    fun update(transform: (LooseGemstoneEditorUiState) -> LooseGemstoneEditorUiState) {
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
                    currency = "USD",
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
                itemRepository.saveItem(item)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.message)
            }
        }
    }
}

private fun AppraisalItem.LooseGemstone.toUiState() = LooseGemstoneEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
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