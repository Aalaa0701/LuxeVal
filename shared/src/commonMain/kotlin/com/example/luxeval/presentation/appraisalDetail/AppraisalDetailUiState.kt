package com.example.luxeval.presentation.appraisalDetail

import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalItem
import com.example.luxeval.domain.AppraisalPurpose

data class ClientFormState(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

data class AppraisalDetailUiState(
    val isLoading: Boolean = true,
    val appraisal: Appraisal? = null,
    val items: List<AppraisalItem> = emptyList(),
    val clientForm: ClientFormState = ClientFormState(),
    val purpose: AppraisalPurpose = AppraisalPurpose.OTHER,
    val internalNotes: String = "",
    val hasUnsavedChanges: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val businessProfileComplete: Boolean = false
) {
    /** Mirrors your mockup: items must exist and client must be filled before finalize/report actions light up. */
    val canFinalize: Boolean
        get() = items.isNotEmpty() && clientForm.name.isNotBlank() && businessProfileComplete

    /** Items with no estimatedValue yet — the total on the report silently
     *  treats these as $0 unless the UI surfaces this explicitly. */
    val unpricedItems: List<AppraisalItem>
        get() = items.filter { it.estimatedValue == null }

    val hasUnpricedItems: Boolean get() = unpricedItems.isNotEmpty()
}
