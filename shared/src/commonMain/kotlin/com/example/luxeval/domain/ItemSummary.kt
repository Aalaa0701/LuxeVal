package com.example.luxeval.domain

data class ItemSummary(
    val id: String,
    val appraisalId: String,
    val itemName: String,
    val category: ItemCategory,
    val estimatedValue: Double?,
    val currency: String,
    val primaryPhotoUri: String?,
    val createdAt: Long
)