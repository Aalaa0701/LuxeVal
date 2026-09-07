package com.example.luxeval.presentation.report

import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalItem
import com.example.luxeval.domain.Client

data class ReportItemRow(
    val item: AppraisalItem,
    val specs: List<SpecLine>
)

data class ReportBusinessInfo(
    val businessName: String,
    val licenseNumber: String,
    val businessAddress: String,
    val businessPhone: String,
    val logoPath: String?,
    val signaturePath: String?
)

data class ReportPreviewUiState(
    val isLoading: Boolean = true,
    val appraisal: Appraisal? = null,
    val client: Client? = null,
    val items: List<ReportItemRow> = emptyList(),
    val business: ReportBusinessInfo = ReportBusinessInfo("", "", "", "", null, null),
    val currencyCode: String = "USD",
    val isExporting: Boolean = false,
    val exportError: String? = null
) {
    val totalValue: Double get() = items.sumOf { it.item.estimatedValue ?: 0.0 }
    val unpricedItemCount: Int get() = items.count { it.item.estimatedValue == null }
    val hasUnpricedItems: Boolean get() = unpricedItemCount > 0
    val reportDate: Long get() = appraisal?.appraisalDate ?: 0L
}
