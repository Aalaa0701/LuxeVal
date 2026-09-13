package com.example.luxeval.presentation.itemEditor.pearl

import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.MeasurementUnit
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.itemEditor.common.PearlFormState
import com.example.luxeval.presentation.util.UserError

data class PearlEditorUiState(
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

    val pearl: PearlFormState = PearlFormState(),
    val hasClaspMetal: Boolean = false,   // mirrors gemstone's optional-toggle pattern
    val metal: MetalFormState = MetalFormState(),

    val estimatedValue: String = "",
    val notes: String = "",
    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
    val currencyCode: String = "USD",
    val measurementUnit: MeasurementUnit = MeasurementUnit.METRIC
) {
    val canSave: Boolean get() = itemName.isNotBlank()
}