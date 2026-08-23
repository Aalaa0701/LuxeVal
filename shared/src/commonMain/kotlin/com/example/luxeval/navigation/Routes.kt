package com.example.luxeval.navigation

import kotlinx.serialization.Serializable

@Serializable data object MainGraph
// Auth
@Serializable data object Splash
@Serializable data object Login
@Serializable data object SignUp
@Serializable data object ResetPassword

// Main (bottom-nav) destinations
@Serializable data object Dashboard
@Serializable data object AppraisalsList
@Serializable data object Search
@Serializable data object Settings

// Flow destinations
@Serializable data class AppraisalDetail(val appraisalId: String)
@Serializable data class ItemEditor(val appraisalId: String, val category: String, val itemId: String)
@Serializable data class ReportPreview(val appraisalId: String)