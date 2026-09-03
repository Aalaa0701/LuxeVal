package com.example.luxeval.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.presentation.util.AppraisalCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val profileStore: AppraiserProfileStore,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        combine(
            appraisalRepository.observeAll(),
            clientRepository.observeClients(),
            itemRepository.observeAllItemSummaries()
        ) { appraisals, clients, items -> Triple(appraisals, clients, items) }
            .onEach { (appraisals, clients, items) ->
                val itemsByAppraisal = items.groupBy { it.appraisalId }
                val clientsById = clients.associateBy { it.id }

                fun cardFor(appraisal: Appraisal): AppraisalCard {
                    val appraisalItems = itemsByAppraisal[appraisal.id].orEmpty()
                    val itemForDisplay = appraisalItems.filter { it.primaryPhotoUri != null }.maxByOrNull { it.createdAt }
                        ?: appraisalItems.maxByOrNull { it.createdAt }
                    return AppraisalCard(
                        appraisal = appraisal,
                        representativeItem = itemForDisplay,
                        clientName = appraisal.clientId?.let { clientsById[it]?.name },
                        totalValue = appraisalItems.sumOf { it.estimatedValue ?: 0.0 }
                    )
                }

                val continueDraft = appraisals
                    .filter { it.status == AppraisalStatus.DRAFT }
                    .maxByOrNull { it.updatedAt }
                    ?.let(::cardFor)

                val recent = appraisals
                    .sortedByDescending { it.updatedAt }
                    .take(4)
                    .map(::cardFor)

                _uiState.value = DashboardUiState(
                    isLoading = false,
                    totalAppraisals = appraisals.size,
                    completedCount = appraisals.count { it.status == AppraisalStatus.COMPLETED },
                    draftsCount = appraisals.count { it.status == AppraisalStatus.DRAFT },
                    customersCount = clients.size,
                    continueDraft = continueDraft,
                    recentAppraisals = recent
                )
            }
            .launchIn(viewModelScope)
    }

    /** Called from the "+ New Appraisal" FAB. Returns the new id so the caller can navigate. */
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