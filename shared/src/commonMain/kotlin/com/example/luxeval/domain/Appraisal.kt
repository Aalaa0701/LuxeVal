package com.example.luxeval.domain

enum class AppraisalStatus { DRAFT, COMPLETED, ARCHIVED }

data class Appraisal(
    val id: String,
    val clientId: String,
    val title: String? = null,
    val status: AppraisalStatus = AppraisalStatus.DRAFT,
    val appraisalDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)