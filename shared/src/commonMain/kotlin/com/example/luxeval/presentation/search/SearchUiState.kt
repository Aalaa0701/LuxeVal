package com.example.luxeval.presentation.search


import com.example.luxeval.domain.Client
import com.example.luxeval.presentation.util.AppraisalCard

data class SearchUiState(
    val query: String = "",
    val hasSearched: Boolean = false,
    val appraisalResults: List<AppraisalCard> = emptyList(),
    val clientResults: List<Client> = emptyList()
) {
    val hasResults: Boolean get() = appraisalResults.isNotEmpty() || clientResults.isNotEmpty()
}