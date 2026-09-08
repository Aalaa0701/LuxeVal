package com.example.luxeval.presentation.itemEditor.necklace


import com.example.luxeval.domain.ClaspType
import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.util.UserError

data class NecklaceEditorUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isNewItem: Boolean = true,
    val saveError: UserError? = null,

    // Shared base-item fields — identical shape to RingEditorUiState
    val photoUris: List<String> = emptyList(),
    val itemName: String = "",
    val condition: ItemCondition = ItemCondition.EXCELLENT,
    val conditionNotes: String = "",
    val hallmarks: String = "",
    val grossWeightGrams: String = "",

    // Necklace-specific fields — this is the whole delta from Ring
    val lengthCm: String = "",
    val chainStyle: String = "",
    val claspType: ClaspType? = null,
    val hasPendantAttached: Boolean = false,

    val metal: MetalFormState = MetalFormState(),
    val gemstone: GemstoneFormState = GemstoneFormState(),

    val estimatedValue: String = "",
    val notes: String = "",

    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
) {
    val canSave: Boolean get() = itemName.isNotBlank() && metal.purity.isNotBlank()
}