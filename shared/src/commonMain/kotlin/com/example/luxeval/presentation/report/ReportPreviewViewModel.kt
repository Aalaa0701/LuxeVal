package com.example.luxeval.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luxeval.data.AppSettingsStore
import com.example.luxeval.data.AppraiserProfileStore
import com.example.luxeval.data.repository.AppraisalRepository
import com.example.luxeval.data.repository.ClientRepository
import com.example.luxeval.data.repository.ItemRepository
import com.example.luxeval.data.repository.PhotoRepository
import com.example.luxeval.domain.withPhotoUris
import com.example.luxeval.presentation.share.FileSharer
import io.ktor.util.logging.Logger
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class ReportPreviewViewModel(
    private val appraisalId: String,
    private val appraisalRepository: AppraisalRepository,
    private val clientRepository: ClientRepository,
    private val itemRepository: ItemRepository,
    private val profileStore: AppraiserProfileStore,
    private val settingsStore: AppSettingsStore,
    private val pdfExporter: PdfExporter,
    private val fileSharer: FileSharer,
    private val photoRepository: PhotoRepository
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
    fun dismissExportError() {
        _uiState.value = _uiState.value.copy(exportError = null)
    }
    fun exportPdf(onExported: (String) -> Unit, onError: (String) -> Unit) {
        println("exportPdf: starting")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportError = null)

            println("exportPdf: isExporting set true, uiState now = ${_uiState.value.isExporting}")
            try {
                val stateWithResolvedUrls = resolveAllPhotoUrls(uiState.value)
                val html = ReportHtmlBuilder.build(uiState.value)
                println("exportPdf: html built, length=${html.length}")
                val fileName = "${uiState.value.appraisal?.documentId ?: "report"}.pdf"
                val path = withTimeout(15_000) {
                    pdfExporter.exportToPdf(html, fileName)
                }
                println("exportPdf: got path=$path")
                _uiState.value = _uiState.value.copy(isExporting = false)
                onExported(path)
            } catch (e: TimeoutCancellationException) {
                println("exportPdf: TIMED OUT")
                _uiState.value = _uiState.value.copy(isExporting = false, exportError = "PDF export timed out")
                onError("PDF export timed out")
            } catch (e: Exception) {
                println("exportPdf: EXCEPTION ${e::class.simpleName}: ${e.message}")
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isExporting = false, exportError = e.message)
                onError(e.message ?: "Export failed")
            }
        }
    }
    private suspend fun resolveAllPhotoUrls(state: ReportPreviewUiState): ReportPreviewUiState {
        val resolvedItems = state.items.map { row ->
            row.copy(item = row.item.withPhotoUris(row.item.photoUris.map { photoRepository.resolveUrl(it) }))
        }
        val resolvedBusiness = state.business.copy(
            logoPath = state.business.logoPath?.let { photoRepository.resolveUrl(it) },
            signaturePath = state.business.signaturePath?.let { photoRepository.resolveUrl(it) }
        )
        return state.copy(items = resolvedItems, business = resolvedBusiness)
    }
    fun shareFile(path: String) {
        fileSharer.shareFile(path)
    }
//    fun exportPdf(onExported: (path: String) -> Unit, onError: (String) -> Unit) {
//
//        viewModelScope.launch {
//            try {
//                val html = ReportHtmlBuilder.build(uiState.value)
//                val fileName = "${uiState.value.appraisal?.documentId ?: "report"}.pdf"
//                val path = pdfExporter.exportToPdf(html, fileName)
//                onExported(path)
//            } catch (e: Exception) {
//                onError(e.message ?: "Export failed")
//            }
//        }
//    }
}