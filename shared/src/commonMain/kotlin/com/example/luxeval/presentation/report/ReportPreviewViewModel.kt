package com.example.luxeval.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import io.ktor.util.logging.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ReportPreviewViewModel(
    private val appraisalId: String,
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val profileStore: AppraiserProfileStore,
    private val settingsStore: AppSettingsStore,
    private val pdfExporter: PdfExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportPreviewUiState())
    val uiState: StateFlow<ReportPreviewUiState> = _uiState.asStateFlow()

    init {
        combine(
            appraisalRepository.observeById(appraisalId),
            itemRepository.observeItemsForAppraisal(appraisalId)
        ) { appraisal, items -> appraisal to items }
            .onEach { (appraisal, items) ->
                val client = appraisal?.clientId?.let { clientRepository.getClient(it) }
                _uiState.value = ReportPreviewUiState(
                    isLoading = false,
                    appraisal = appraisal,
                    client = client,
                    items = items.map { ReportItemRow(item = it, specs = it.reportSpecs()) },
                    business = ReportBusinessInfo(
                        businessName = profileStore.getName(),
                        licenseNumber = profileStore.getLicenseNumber(),
                        businessAddress = profileStore.getBusinessAddress(),
                        businessPhone = profileStore.getBusinessPhone(),
                        logoPath = profileStore.getLogoPath(),
                        signaturePath = profileStore.getSignaturePath()
                    ),
                    currencyCode = settingsStore.getCurrencyCode()
                )
            }
            .launchIn(viewModelScope)
    }
    fun exportPdf(onExported: (path: String) -> Unit, onError: (String) -> Unit) {

        viewModelScope.launch {
            try {
                val html = ReportHtmlBuilder.build(uiState.value)
                val fileName = "${uiState.value.appraisal?.documentId ?: "report"}.pdf"
                val path = pdfExporter.exportToPdf(html, fileName)
                onExported(path)
            } catch (e: Exception) {
                onError(e.message ?: "Export failed")
            }
        }
    }
}