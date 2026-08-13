package com.example.luxeval.domain

enum class AppraisalStatus { DRAFT, COMPLETED, ARCHIVED }

enum class AppraisalPurpose {
    INSURANCE_REPLACEMENT, FAIR_MARKET_VALUE, ESTATE_VALUATION, WHOLESALE_LIQUIDATION, OTHER
}
data class Appraisal(
    val id: String,
    val documentId: String,
    val clientId: String?,
    val title: String? = null,
    val purpose: AppraisalPurpose,
    val status: AppraisalStatus = AppraisalStatus.DRAFT,
    val internalNotes: String? = null,
    val appraisalDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)