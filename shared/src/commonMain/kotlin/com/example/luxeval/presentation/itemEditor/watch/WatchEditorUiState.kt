package com.example.luxeval.presentation.itemEditor.watch

import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.domain.WatchComplication
import com.example.luxeval.domain.WatchMovementType
import com.example.luxeval.domain.WatchRunningCondition
import com.example.luxeval.presentation.util.UserError

data class WatchEditorUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isNewItem: Boolean = true,
    val saveError: UserError? = null,

    val photoUris: List<String> = emptyList(),
    val itemName: String = "",
    val condition: ItemCondition = ItemCondition.EXCELLENT,
    val conditionNotes: String = "",
    val hallmarks: String = "",
    val grossWeightGrams: String = "",

    val brand: String = "",
    val model: String = "",
    val referenceNumber: String = "",
    val movementType: WatchMovementType = WatchMovementType.AUTOMATIC,
    val caseMaterial: String = "",
    val caseSizeMm: String = "",
    val dialColor: String = "",
    val complications: Set<WatchComplication> = emptySet(),
    val bandMaterial: String = "",
    val waterResistance: String = "",
    val serialNumber: String = "",
    val hasBox: Boolean = false,
    val hasPapers: Boolean = false,
    val runningCondition: WatchRunningCondition = WatchRunningCondition.RUNNING_ACCURATELY,
    val yearOfManufacture: String = "",

    val estimatedValue: String = "",
    val notes: String = "",

    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
) {
    val canSave: Boolean get() = itemName.isNotBlank() && brand.isNotBlank()
}