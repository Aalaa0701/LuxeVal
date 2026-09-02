package com.example.luxeval.presentation.itemEditor.pearl

import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.itemEditor.common.MetalFormState
import com.example.luxeval.presentation.itemEditor.common.PearlFormState

data class PearlEditorUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isNewItem: Boolean = true,
    val saveError: String? = null,

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
) {
    val canSave: Boolean get() = itemName.isNotBlank()
}