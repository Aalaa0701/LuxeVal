// presentation/itemEditor/brooch/BroochEditorUiState.kt
package com.example.luxeval.presentation.itemEditor.brooch

import com.example.luxeval.domain.BroochPinMechanism
import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.MeasurementUnit
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.util.UserError

data class BroochEditorUiState(
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

    val pinMechanism: BroochPinMechanism = BroochPinMechanism.C_CLASP,
    val heightMm: String = "",
    val widthMm: String = "",
    val motifDescription: String = "",

    val metal: MetalFormState = MetalFormState(),
    val gemstone: GemstoneFormState = GemstoneFormState(),

    val estimatedValue: String = "",
    val notes: String = "",
    val valueSource: ValueSource = ValueSource.MANUAL,
    val isFetchingPrice: Boolean = false,
    val currencyCode: String = "USD",
    val measurementUnit: MeasurementUnit = MeasurementUnit.METRIC
) {
    val canSave: Boolean get() = itemName.isNotBlank() && metal.purity.isNotBlank()
}