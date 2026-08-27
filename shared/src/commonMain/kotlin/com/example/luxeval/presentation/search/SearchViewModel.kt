package com.example.luxeval.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.presentation.util.AppraisalCard
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        combine(
            queryFlow.debounce(250).distinctUntilChanged(),
            appraisalRepository.observeAll(),
            clientRepository.observeClients(),
            itemRepository.observeAllItemSummaries()
        ) { query, appraisals, clients, items ->
            if (query.isBlank()) {
                return@combine SearchUiState(query = query, hasSearched = false)
            }
            val itemsByAppraisal = items.groupBy { it.appraisalId }
            val clientsById = clients.associateBy { it.id }

            val matchingAppraisals = appraisals
                .filter { appraisal ->
                    val client = appraisal.clientId?.let { clientsById[it] }
                    val appraisalItems = itemsByAppraisal[appraisal.id].orEmpty()
                    appraisal.documentId.contains(query, ignoreCase = true) ||
                            client?.name?.contains(query, ignoreCase = true) == true ||
                            appraisalItems.any { it.itemName.contains(query, ignoreCase = true) }
                }
                // SearchViewModel.kt — inside the combine block, replace the .map { appraisal -> ... } for matchingAppraisals with:
                .map { appraisal ->
                    val appraisalItems = itemsByAppraisal[appraisal.id].orEmpty()
                    val itemForDisplay = appraisalItems.filter { it.primaryPhotoUri != null }.maxByOrNull { it.createdAt }
                        ?: appraisalItems.maxByOrNull { it.createdAt }
                    AppraisalCard(
                        appraisal = appraisal,
                        representativeItem = itemForDisplay,
                        clientName = appraisal.clientId?.let { clientsById[it]?.name },
                        totalValue = appraisalItems.sumOf { it.estimatedValue ?: 0.0 }
                    )
                }

            val matchingClients = clients.filter {
                it.name.contains(query, ignoreCase = true) || it.email?.contains(query, ignoreCase = true) == true
            }

            SearchUiState(
                query = query,
                hasSearched = true,
                appraisalResults = matchingAppraisals,
                clientResults = matchingClients
            )
        }
            // Only apply the debounced result to the results fields — never let it
            // clobber `query`, which is already being driven instantly below.
            .onEach { debouncedState ->
                _uiState.value = _uiState.value.copy(
                    hasSearched = debouncedState.hasSearched,
                    appraisalResults = debouncedState.appraisalResults,
                    clientResults = debouncedState.clientResults
                )
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(value: String) {
         _uiState.value = _uiState.value.copy(query = value)
        queryFlow.value = value
    }

    fun clearSearch() {
        onQueryChanged("")
    }
}