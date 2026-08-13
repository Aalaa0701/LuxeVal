package com.example.luxeval.presentation.appraisalsList

import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.presentation.util.AppraisalCard

enum class AppraisalFilter { ALL, DRAFTS, COMPLETED, ARCHIVED }
enum class AppraisalSort { DATE_ADDED, ESTIMATED_VALUE, CLIENT_NAME }

data class AppraisalsListUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val filter: AppraisalFilter = AppraisalFilter.ALL,
    val sort: AppraisalSort = AppraisalSort.DATE_ADDED,
    val cards: List<AppraisalCard> = emptyList()
)

internal fun AppraisalFilter.toStatus(): AppraisalStatus? = when (this) {
    AppraisalFilter.ALL -> null
    AppraisalFilter.DRAFTS -> AppraisalStatus.DRAFT
    AppraisalFilter.COMPLETED -> AppraisalStatus.COMPLETED
    AppraisalFilter.ARCHIVED -> AppraisalStatus.ARCHIVED
}