package com.example.luxeval.presentation.itemEditor.ring

import com.example.luxeval.domain.*
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.util.UserError

data class RingEditorUiState(
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

    val ringSize: String = "",
    val bandWidthMm: String = "",
    val bandStyle: RingBandStyle = RingBandStyle.SOLITAIRE,
    val shankType: ShankType? = null,
    val engraving: String = "",

    val metal: MetalFormState = MetalFormState(),
    val gemstone: GemstoneFormState = GemstoneFormState(),

    val estimatedValue: String = "",
    val notes: String = "",
    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
) {
    /** Gate for the Save button — mirrors AppraisalDetailUiState.canFinalize style. */
    val canSave: Boolean get() = itemName.isNotBlank() && metal.purity.isNotBlank()
}