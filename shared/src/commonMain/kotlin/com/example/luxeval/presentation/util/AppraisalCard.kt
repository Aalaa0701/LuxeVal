package com.example.luxeval.presentation.util


import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.ItemSummary

data class AppraisalCard(
    val appraisal: Appraisal,
    val representativeItem: ItemSummary?,
    val clientName: String?,
    val totalValue: Double = 0.0
)