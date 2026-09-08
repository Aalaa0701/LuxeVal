package com.example.luxeval.presentation.itemEditor.loose_gemstone

import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.util.UserError

data class LooseGemstoneEditorUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isNewItem: Boolean = true,
    val saveError: UserError? = null,

    val photoUris: List<String> = emptyList(),
    val itemName: String = "",
    val condition: ItemCondition = ItemCondition.EXCELLENT,
    val conditionNotes: String = "",
    val grossWeightGrams: String = "",

    // enabled/toggle from GemstoneFormState is irrelevant here — always treated as on
    val gemstone: GemstoneFormState = GemstoneFormState(enabled = true),

    val estimatedValue: String = "",
    val notes: String = "",

    val isFetchingPrice: Boolean = false,
    val valueSource: ValueSource = ValueSource.MANUAL,
) {
    val canSave: Boolean get() = itemName.isNotBlank() && gemstone.caratWeight.isNotBlank()
}