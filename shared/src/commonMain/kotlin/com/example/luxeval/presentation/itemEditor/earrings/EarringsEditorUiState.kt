// presentation/itemEditor/earrings/EarringsEditorUiState.kt
package com.example.luxeval.presentation.itemEditor.earrings

import com.example.luxeval.domain.EarringBacking
import com.example.luxeval.domain.EarringStyle
import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.MeasurementUnit
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.util.UserError

data class EarringsEditorUiState(
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

    val style: EarringStyle = EarringStyle.STUD,
    val backing: EarringBacking = EarringBacking.PUSH_BACK,
    val isConfirmedPair: Boolean = true,
    val hoopDiameterMm: String = "",

    val metal: MetalFormState = MetalFormState(),
    val gemstone: GemstoneFormState = GemstoneFormState(),

    val estimatedValue: String = "",
    val notes: String = "",

    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
    val currencyCode: String = "USD",
    val measurementUnit: MeasurementUnit = MeasurementUnit.METRIC
) {
    val canSave: Boolean get() = itemName.isNotBlank() && metal.purity.isNotBlank()
}