package com.example.luxeval.presentation.appraisalDetail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.SyncRepository
import com.example.luxeval.domain.AppraisalPurpose
import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.domain.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AppraisalDetailViewModel(
    private val appraisalId: String,
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val profileStore: AppraiserProfileStore,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppraisalDetailUiState())
    val uiState: StateFlow<AppraisalDetailUiState> = _uiState.asStateFlow()

    // Baseline loaded from DB — used to detect whether local edits diverge from it.
    private var loadedClient: Client? = null
    private var loadedPurpose: AppraisalPurpose = AppraisalPurpose.OTHER
    private var loadedNotes: String = ""

    init {
        _uiState.value = _uiState.value.copy(
            businessProfileComplete = profileStore.isProfileComplete()
        )
        combine(
            appraisalRepository.observeById(appraisalId),
            itemRepository.observeItemsForAppraisal(appraisalId)
        ) { appraisal, items -> appraisal to items }
            .onEach { (appraisal, items) ->
                if (appraisal == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@onEach
                }
                // Only re-seed the form from DB on first load, so we don't clobber
                // in-progress typing every time the Flow re-emits (e.g. after an item is added).
                val isFirstLoad = _uiState.value.appraisal == null
                if (isFirstLoad) {
                    val client = appraisal.clientId?.let { clientRepository.getClient(it) }
                    loadedClient = client
                    loadedPurpose = appraisal.purpose
                    loadedNotes = appraisal.internalNotes.orEmpty()
                    _uiState.value = _uiState.value.copy(
                        clientForm = ClientFormState(
                            name = client?.name.orEmpty(),
                            email = client?.email.orEmpty(),
                            phone = client?.phone.orEmpty()
                        ),
                        purpose = appraisal.purpose,
                        internalNotes = appraisal.internalNotes.orEmpty()
                    )
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    appraisal = appraisal,
                    items = items
                )
            }
            .launchIn(viewModelScope)
    }

    fun onClientNameChanged(value: String) = updateForm { it.copy(name = value) }
    fun onClientEmailChanged(value: String) = updateForm { it.copy(email = value) }
    fun onClientPhoneChanged(value: String) = updateForm { it.copy(phone = value) }
    fun markCompleted() {
        val appraisal = _uiState.value.appraisal ?: return
        if (!_uiState.value.canFinalize) return
        viewModelScope.launch {
            val updated = appraisal.copy(
                status = AppraisalStatus.COMPLETED,
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            appraisalRepository.save(updated)
            syncRepository.pushAppraisalAsync(updated)
            // no need to touch _uiState manually — observeById() already re-emits
            // the updated appraisal through the existing combine/onEach pipeline
        }
    }
    fun onPurposeChanged(value: AppraisalPurpose) {
        _uiState.value = _uiState.value.copy(purpose = value)
        recomputeDirty()
    }

    fun onNotesChanged(value: String) {
        _uiState.value = _uiState.value.copy(internalNotes = value)
        recomputeDirty()
    }

    private inline fun updateForm(transform: (ClientFormState) -> ClientFormState) {
        _uiState.value = _uiState.value.copy(clientForm = transform(_uiState.value.clientForm))
        recomputeDirty()
    }

    private fun recomputeDirty() {
        val state = _uiState.value
        val clientChanged = state.clientForm.name != loadedClient?.name.orEmpty() ||
                state.clientForm.email != loadedClient?.email.orEmpty() ||
                state.clientForm.phone != loadedClient?.phone.orEmpty()
        val purposeChanged = state.purpose != loadedPurpose
        val notesChanged = state.internalNotes != loadedNotes
        _uiState.value = state.copy(hasUnsavedChanges = clientChanged || purposeChanged || notesChanged)
    }

    fun save() {
        val state = _uiState.value
        val appraisal = state.appraisal ?: return
        if (!state.hasUnsavedChanges || state.clientForm.name.isBlank()) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, saveError = null)
            try {
                @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
                val client = Client(
                    id = loadedClient?.id ?: Uuid.random().toString(),
                    name = state.clientForm.name,
                    email = state.clientForm.email.ifBlank { null },
                    phone = state.clientForm.phone.ifBlank { null }
                )
                clientRepository.saveClient(client)
                syncRepository.pushClientAsync(client)
                val updatedAppraisal = appraisal.copy(
                    clientId = client.id,
                    purpose = state.purpose,
                    internalNotes = state.internalNotes.ifBlank { null },
                    updatedAt = Clock.System.now().toEpochMilliseconds()
                )
                appraisalRepository.save(updatedAppraisal)
                syncRepository.pushAppraisalAsync(updatedAppraisal)

                loadedClient = client
                loadedPurpose = state.purpose
                loadedNotes = state.internalNotes
                _uiState.value = _uiState.value.copy(isSaving = false, hasUnsavedChanges = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, saveError = e.message)
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId)
            syncRepository.deleteItemRemoteAsync(itemId)
        }
    }
}