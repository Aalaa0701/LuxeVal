package com.example.luxeval.presentation.itemEditor.bracelet

import com.example.luxeval.domain.BraceletStyle
import com.example.luxeval.domain.ClaspType
import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.util.UserError

data class BraceletEditorUiState(
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

    val lengthCm: String = "",
    val style: BraceletStyle = BraceletStyle.TENNIS,
    val claspType: ClaspType = ClaspType.LOBSTER_CLAW,   // defaulted, not nullable — see Necklace note
    val isAdjustable: Boolean = false,
    val charmCount: String = "",
    val charmDescriptions: String = "",   // multi-line free text; split on save

    val metal: MetalFormState = MetalFormState(),
    val gemstone: GemstoneFormState = GemstoneFormState(),

    val estimatedValue: String = "",
    val notes: String = "",

    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
) {
    val canSave: Boolean get() = itemName.isNotBlank() && metal.purity.isNotBlank()
}