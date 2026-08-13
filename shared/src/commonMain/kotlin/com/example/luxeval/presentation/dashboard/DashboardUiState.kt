package com.example.luxeval.presentation.dashboard

import com.example.luxeval.presentation.util.AppraisalCard

data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalAppraisals: Int = 0,
    val completedCount: Int = 0,
    val draftsCount: Int = 0,
    val customersCount: Int = 0,
    val continueDraft: AppraisalCard? = null,   // most recently updated draft, if any
    val recentAppraisals: List<AppraisalCard> = emptyList()
)