// presentation/itemEditor/watch/WatchEditorViewModel.kt
package com.example.luxeval.presentation.itemEditor.watch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PhotoRepository
import com.example.luxeval.data.repository.PricingRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.photo.PhotoStorage
import com.example.luxeval.presentation.util.ErrorContext
import com.example.luxeval.presentation.util.toUserError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

class WatchEditorViewModel(
    private val appraisalId: String,
    private val itemId: String,
    private val itemRepository: ItemRepository,
    private val photoStorage: PhotoStorage,
    private val pricingRepository: PricingRepository,
    private val syncRepository: SyncRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchEditorUiState())
    val uiState: StateFlow<WatchEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existing = itemRepository.getWatch(itemId)
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
    fun removePhoto(uri: String) {
        _uiState.value = _uiState.value.copy(photoUris = _uiState.value.photoUris - uri)
        viewModelScope.launch {
            if (uri.startsWith("supabase://")) photoRepository.deletePhoto(uri)
            else photoStorage.deleteImage(uri)
        }
    }

    fun update(transform: (WatchEditorUiState) -> WatchEditorUiState) {
        _uiState.value = transform(_uiState.value)
    }
//    fun removePhoto(uri: String) {
//        _uiState.value = _uiState.value.copy(photoUris = _uiState.value.photoUris - uri)
//        viewModelScope.launch { photoStorage.deleteImage(uri) }
//    }

    fun toggleComplication(complication: WatchComplication) {
        _uiState.value = _uiState.value.let { s ->
            s.copy(
                complications = if (complication in s.complications)
                    s.complications - complication
                else
                    s.complications + complication
            )
        }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, saveError = null)
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val watch = AppraisalItem.Watch(
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
                    brand = state.brand,
                    model = state.model.ifBlank { null },
                    referenceNumber = state.referenceNumber.ifBlank { null },
                    movementType = state.movementType,
                    caseMaterial = state.caseMaterial,
                    caseSizeMm = state.caseSizeMm.toDoubleOrNull(),
                    dialColor = state.dialColor.ifBlank { null },
                    complications = state.complications.toList(),
                    bandMaterial = state.bandMaterial.ifBlank { null },
                    waterResistance = state.waterResistance.ifBlank { null },
                    serialNumber = state.serialNumber.ifBlank { null },
                    hasBox = state.hasBox,
                    hasPapers = state.hasPapers,
                    runningCondition = state.runningCondition,
                    yearOfManufacture = state.yearOfManufacture.toIntOrNull()
                )
                val savedWatch = itemRepository.saveItem(watch)
                syncRepository.pushItemAsync(savedWatch)
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

private fun AppraisalItem.Watch.toUiState() = WatchEditorUiState(
    isNewItem = false,
    photoUris = photoUris,
    itemName = itemName,
    condition = condition,
    conditionNotes = conditionNotes.orEmpty(),
    hallmarks = hallmarks.orEmpty(),
    grossWeightGrams = grossWeightGrams?.toString().orEmpty(),
    brand = brand,
    model = model.orEmpty(),
    referenceNumber = referenceNumber.orEmpty(),
    movementType = movementType,
    caseMaterial = caseMaterial,
    caseSizeMm = caseSizeMm?.toString().orEmpty(),
    dialColor = dialColor.orEmpty(),
    complications = complications.toSet(),
    bandMaterial = bandMaterial.orEmpty(),
    waterResistance = waterResistance.orEmpty(),
    serialNumber = serialNumber.orEmpty(),
    hasBox = hasBox,
    hasPapers = hasPapers,
    runningCondition = runningCondition,
    yearOfManufacture = yearOfManufacture?.toString().orEmpty(),
    estimatedValue = estimatedValue?.toString().orEmpty(),
    notes = notes.orEmpty()
)