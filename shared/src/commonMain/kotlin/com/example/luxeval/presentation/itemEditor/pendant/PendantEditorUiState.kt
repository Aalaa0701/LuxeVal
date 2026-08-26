package com.example.luxeval.presentation.itemEditor.pendant

import com.example.luxeval.domain.ItemCondition
import com.example.luxeval.presentation.itemEditor.common.GemstoneFormState
import com.example.luxeval.presentation.itemEditor.common.MetalFormState

data class PendantEditorUiState(
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

    val bailType: String = "",
    val heightMm: String = "",
    val widthMm: String = "",
    val soldWithChain: Boolean = false,

    val metal: MetalFormState = MetalFormState(),
    val gemstone: GemstoneFormState = GemstoneFormState(),

    val estimatedValue: String = "",
    val notes: String = ""
) {
    val canSave: Boolean get() = itemName.isNotBlank() && metal.purity.isNotBlank()
}