package com.example.luxeval.presentation.appraisalsList


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.presentation.util.AppraisalCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppraisalsListViewModel(
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val profileStore: AppraiserProfileStore,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppraisalsListUiState())
    val uiState: StateFlow<AppraisalsListUiState> = _uiState.asStateFlow()

    init {
        combine(
            appraisalRepository.observeAll(),
            clientRepository.observeClients(),
            itemRepository.observeAllItemSummaries()
        ) { appraisals, clients, items -> Triple(appraisals, clients, items) }
            .onEach { (appraisals, clients, items) ->
                val itemsByAppraisal = items.groupBy { it.appraisalId }
                val clientsById = clients.associateBy { it.id }
                // AppraisalsListViewModel.kt — inside .onEach, replace the allCards map with:
                val allCards = appraisals.map { appraisal ->
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
                _uiState.value = _uiState.value.copy(isLoading = false, cards = applyFilters(allCards))
                allLoadedCards = allCards
            }
            .launchIn(viewModelScope)
    }

    // Cached so filter/sort/search changes don't need to re-observe the DB.
    private var allLoadedCards: List<AppraisalCard> = emptyList()

    fun onQueryChanged(value: String) {
        _uiState.value = _uiState.value.copy(query = value, cards = applyFilters(allLoadedCards, query = value))
    }

    fun onFilterChanged(value: AppraisalFilter) {
        _uiState.value = _uiState.value.copy(filter = value, cards = applyFilters(allLoadedCards, filter = value))
    }

    fun onSortChanged(value: AppraisalSort) {
        _uiState.value = _uiState.value.copy(sort = value, cards = applyFilters(allLoadedCards, sort = value))
    }

    private fun applyFilters(
        cards: List<AppraisalCard>,
        query: String = _uiState.value.query,
        filter: AppraisalFilter = _uiState.value.filter,
        sort: AppraisalSort = _uiState.value.sort
    ): List<AppraisalCard> {
        val status = filter.toStatus()
        val filtered = cards
            .filter { status == null || it.appraisal.status == status }
            .filter { card ->
                if (query.isBlank()) return@filter true
                val q = query.trim()
                card.appraisal.documentId.contains(q, ignoreCase = true) ||
                        card.clientName?.contains(q, ignoreCase = true) == true ||
                        card.representativeItem?.itemName?.contains(q, ignoreCase = true) == true
            }
        return when (sort) {
            AppraisalSort.DATE_ADDED -> filtered.sortedByDescending { it.appraisal.createdAt }
            // AppraisalsListViewModel.kt — in applyFilters()
            AppraisalSort.ESTIMATED_VALUE -> filtered.sortedByDescending { it.totalValue }  // was it.representativeItem?.estimatedValue
            AppraisalSort.CLIENT_NAME -> filtered.sortedBy { it.clientName.orEmpty() }
        }
    }

    fun createDraftAndGetId(onCreated: (String) -> Unit, onProfileIncomplete: () -> Unit) {
        if (!profileStore.isProfileComplete()) {
            onProfileIncomplete()
            return
        }
        viewModelScope.launch {
            val draft = appraisalRepository.createDraft()
            syncRepository.pushAppraisalAsync(draft)
            onCreated(draft.id)
        }
    }
}