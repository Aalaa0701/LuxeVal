package com.example.luxeval.presentation.report


import com.example.luxeval.domain.AppraisalPurpose
import com.example.luxeval.presentation.util.formatCurrency
import com.example.luxeval.presentation.util.formatDate

object ReportHtmlBuilder {

    fun build(uiState: ReportPreviewUiState): String {
        val appraisal = requireNotNull(uiState.appraisal)
        val b = uiState.business
        // ReportHtmlBuilder.kt
        return """
    <html>
    <head>
    <meta charset="utf-8">
    <style>
        @page { margin: 32px; }               /* NEW — reapplies on every printed page */
        body { font-family: -apple-system, Helvetica, Arial, sans-serif; color: #0B1C30; margin: 0; } /* margin moved to @page */
        .letterhead { text-align: center; margin-bottom: 24px; }
        .logo { width: 64px; height: 64px; margin-bottom: 8px; }
        .business-name { font-size: 22px; font-weight: 700; margin: 0; }
        .muted { color: #44474C; font-size: 12px; }
        hr { border: none; border-top: 1px solid #C4C6CD; margin: 16px 0; }
        .meta-row { display: flex; justify-content: space-between; font-size: 13px; margin: 4px 0; }
        .meta-label { color: #44474C; }
        .meta-value { font-weight: 600; }
        .section-title { font-size: 16px; font-weight: 700; margin: 24px 0 12px; }
        .item-card { border: 1px solid #D3E4FE; border-radius: 8px; padding: 16px; margin-bottom: 16px; page-break-inside: avoid; }
        .item-photos { display: flex; gap: 6px; margin-bottom: 10px; }
        .item-photos img { width: 70px; height: 70px; object-fit: cover; border-radius: 6px; }
        .item-name { font-size: 15px; font-weight: 700; margin: 0; }
        .item-category { font-size: 11px; color: #44474C; text-transform: capitalize; margin-bottom: 8px; }
        .spec-row { display: flex; justify-content: space-between; font-size: 12px; margin: 2px 0; }
        .item-value-row { display: flex; justify-content: space-between; font-weight: 700; margin-top: 10px; padding-top: 8px; border-top: 1px solid #E5EEFF; }
        .total-box { background: #243447; color: #E5C372; border-radius: 8px; padding: 16px; display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
        .total-label { font-size: 14px; }
        .total-value { font-size: 20px; font-weight: 700; }
        .signature-block { margin-top: 32px; }
        .signature-img { height: 48px; }
        .closing-section { page-break-inside: avoid; }   /* NEW — keeps total + signature glued together */
    </style>
    </head>
    <body>
        ${letterheadHtml(b)}
        ${metaHtml(appraisal, uiState)}
        <div class="section-title">Itemized Valuation</div>
        ${uiState.items.joinToString("") { itemCardHtml(it, uiState.currencyCode) }}
        <div class="closing-section">
            ${totalHtml(uiState.totalValue, uiState.currencyCode)}
            ${signatureHtml(b)}
        </div>
    </body>
    </html>
""".trimIndent()

//        return """
//            <html>
//            <head>
//            <meta charset="utf-8">
//            <style>
//                body { font-family: -apple-system, Helvetica, Arial, sans-serif; color: #0B1C30; margin: 32px; }
//                .letterhead { text-align: center; margin-bottom: 24px; }
//                .logo { width: 64px; height: 64px; margin-bottom: 8px; }
//                .business-name { font-size: 22px; font-weight: 700; margin: 0; }
//                .muted { color: #44474C; font-size: 12px; }
//                hr { border: none; border-top: 1px solid #C4C6CD; margin: 16px 0; }
//                .meta-row { display: flex; justify-content: space-between; font-size: 13px; margin: 4px 0; }
//                .meta-label { color: #44474C; }
//                .meta-value { font-weight: 600; }
//                .section-title { font-size: 16px; font-weight: 700; margin: 24px 0 12px; }
//                .item-card { border: 1px solid #D3E4FE; border-radius: 8px; padding: 16px; margin-bottom: 16px; page-break-inside: avoid; }
//                .item-photos { display: flex; gap: 6px; margin-bottom: 10px; }
//                .item-photos img { width: 70px; height: 70px; object-fit: cover; border-radius: 6px; }
//                .item-name { font-size: 15px; font-weight: 700; margin: 0; }
//                .item-category { font-size: 11px; color: #44474C; text-transform: capitalize; margin-bottom: 8px; }
//                .spec-row { display: flex; justify-content: space-between; font-size: 12px; margin: 2px 0; }
//                .item-value-row { display: flex; justify-content: space-between; font-weight: 700; margin-top: 10px; padding-top: 8px; border-top: 1px solid #E5EEFF; }
//                .total-box { background: #243447; color: #E5C372; border-radius: 8px; padding: 16px; display: flex; justify-content: space-between; align-items: center; margin-top: 8px; }
//                .total-label { font-size: 14px; }
//                .total-value { font-size: 20px; font-weight: 700; }
//                .signature-block { margin-top: 32px; }
//                .signature-img { height: 48px; }
//            </style>
//            </head>
//            <body>
//                ${letterheadHtml(b)}
//                ${metaHtml(appraisal, uiState)}
//                <div class="section-title">Itemized Valuation</div>
//                ${uiState.items.joinToString("") { itemCardHtml(it, uiState.currencyCode) }}
//                ${totalHtml(uiState.totalValue, uiState.currencyCode)}
//                ${signatureHtml(b)}
//            </body>
//            </html>
//        """.trimIndent()
    }
    private fun imgSrc(pathOrUrl: String): String =
        if (pathOrUrl.startsWith("http")) pathOrUrl else "file://$pathOrUrl"
    private fun letterheadHtml(b: ReportBusinessInfo): String = """
        <div class="letterhead">
            ${b.logoPath?.let { """<img class="logo" src="${imgSrc(it)}" />""" } ?: ""}
            <p class="business-name">${b.businessName.ifBlank { "Business Name Not Set" }.esc()}</p>
            ${if (b.licenseNumber.isNotBlank()) """<p class="muted">License #${b.licenseNumber.esc()}</p>""" else ""}
            ${if (b.businessAddress.isNotBlank()) """<p class="muted">${b.businessAddress.esc()}</p>""" else ""}
            ${if (b.businessPhone.isNotBlank()) """<p class="muted">${b.businessPhone.esc()}</p>""" else ""}
        </div>
        <hr/>
    """.trimIndent()

    private fun metaHtml(appraisal: com.example.luxeval.domain.Appraisal, uiState: ReportPreviewUiState): String = """
        <div class="meta-row"><span class="meta-label">Document ID</span><span class="meta-value">${appraisal.documentId.esc()}</span></div>
        <div class="meta-row"><span class="meta-label">Date</span><span class="meta-value">${formatDate(appraisal.appraisalDate)}</span></div>
        <div class="meta-row"><span class="meta-label">Purpose</span><span class="meta-value">${appraisal.purpose.reportLabel()}</span></div>
        <hr/>
        <div class="meta-row"><span class="meta-label">Client</span><span class="meta-value">${(uiState.client?.name ?: "—").esc()}</span></div>
        ${uiState.client?.email?.let { """<div class="meta-row"><span class="meta-label">Email</span><span class="meta-value">${it.esc()}</span></div>""" } ?: ""}
        ${uiState.client?.phone?.let { """<div class="meta-row"><span class="meta-label">Phone</span><span class="meta-value">${it.esc()}</span></div>""" } ?: ""}
    """.trimIndent()

    private fun itemCardHtml(row: ReportItemRow, currencyCode: String): String {
        val photosHtml = if (row.item.photoUris.isNotEmpty()) {
            """<div class="item-photos">${row.item.photoUris.joinToString("") { """<img src="${imgSrc(it)}" />""" }}</div>"""
        } else ""

        val specsHtml = row.specs.joinToString("") {
            """<div class="spec-row"><span>${it.label.esc()}</span><span>${it.value.esc()}</span></div>"""
        }

        val notesHtml = row.item.notes?.let {
            """<div class="muted" style="margin-top:8px;">Notes: ${it.esc()}</div>"""
        } ?: ""

        val valueHtml = row.item.estimatedValue?.let {
            """<div class="item-value-row"><span>Estimated Value</span><span>${formatCurrency(it, currencyCode)}</span></div>"""
        } ?: """<div class="item-value-row muted"><span>Estimated Value</span><span>Pending</span></div>"""

        return """
            <div class="item-card">
                $photosHtml
                <p class="item-name">${row.item.itemName.esc()}</p>
                <p class="item-category">${row.item.category.name.lowercase().replace("_", " ")}</p>
                $specsHtml
                $notesHtml
                $valueHtml
            </div>
        """.trimIndent()
    }

    private fun totalHtml(total: Double, currencyCode: String): String = """
        <div class="total-box">
            <span class="total-label">Total Appraised Value</span>
            <span class="total-value">${formatCurrency(total, currencyCode)}</span>
        </div>
    """.trimIndent()

    private fun signatureHtml(b: ReportBusinessInfo): String = """
        <div class="signature-block">
            <hr/>
            ${b.signaturePath?.let { """<img class="signature-img" src="${imgSrc(it)}" /><br/>""" } ?: ""}
            <p style="margin:4px 0; font-weight:600;">${b.businessName.ifBlank { "Appraiser" }.esc()}</p>
            <p class="muted">Certified Appraiser</p>
        </div>
    """.trimIndent()


    private fun AppraisalPurpose.reportLabel(): String = when (this) {
        AppraisalPurpose.INSURANCE_REPLACEMENT -> "Insurance Replacement"
        AppraisalPurpose.FAIR_MARKET_VALUE -> "Fair Market Value"
        AppraisalPurpose.ESTATE_VALUATION -> "Estate Valuation"
        AppraisalPurpose.WHOLESALE_LIQUIDATION -> "Wholesale Liquidation"
        AppraisalPurpose.OTHER -> "Other"
    }

    /** Minimal HTML-escaping — user-entered text (names, notes, addresses) must never
     *  break out of its tag or inject markup into the generated document. */
    private fun String.esc(): String = this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}