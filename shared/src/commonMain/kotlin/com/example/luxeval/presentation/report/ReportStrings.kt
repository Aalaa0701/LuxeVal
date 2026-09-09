package com.example.luxeval.presentation.report

import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

data class ReportStrings(
    val businessNameNotSet: String,
    val licenseTemplate: String,          // "License #%1$s"
    val documentId: String,
    val date: String,
    val purpose: String,
    val client: String,
    val email: String,
    val phone: String,
    val itemizedValuation: String,
    val partialValuationLabel: String,
    val pendingItemsNoteTemplate: String, // "%1$d item(s)..."
    val notesTemplate: String,            // "Notes: %1$s"
    val estimatedValue: String,
    val pending: String,
    val grossWeight: String,
    val hallmarks: String,
    val totalAppraisedValue: String,
    val certifiedAppraiser: String,
    val appraiser: String,
    val disclaimer: String,
    val purposeInsurance: String,
    val purposeFairMarket: String,
    val purposeEstate: String,
    val purposeWholesale: String,
    val purposeOther: String
) {
    companion object {
        suspend fun resolve(): ReportStrings = ReportStrings(
            businessNameNotSet = getString(Res.string.report_business_name_not_set),
            licenseTemplate = getString(Res.string.report_license_template), // add plain %1$s template key — see below
            documentId = getString(Res.string.report_document_id),
            date = getString(Res.string.report_date),
            purpose = getString(Res.string.report_purpose),
            client = getString(Res.string.report_client),
            email = getString(Res.string.report_email),
            phone = getString(Res.string.report_phone),
            itemizedValuation = getString(Res.string.report_itemized_valuation),
            partialValuationLabel = getString(Res.string.report_partial_valuation_label),
            pendingItemsNoteTemplate = getString(Res.string.report_pending_items_note),
            notesTemplate = getString(Res.string.report_notes_template), // add plain %1$s template key
            estimatedValue = getString(Res.string.report_estimated_value),
            pending = getString(Res.string.report_pending_label), // add plain non-count key
            grossWeight = getString(Res.string.report_gross_weight),
            hallmarks = getString(Res.string.report_hallmarks),
            totalAppraisedValue = getString(Res.string.report_total_appraised_value),
            certifiedAppraiser = getString(Res.string.report_certified_appraiser),
            appraiser = getString(Res.string.report_appraiser_name),
            disclaimer = getString(Res.string.report_disclaimer),
            purposeInsurance = getString(Res.string.report_purpose_insurance_replacement),
            purposeFairMarket = getString(Res.string.report_purpose_fair_market_value),
            purposeEstate = getString(Res.string.report_purpose_estate_valuation),
            purposeWholesale = getString(Res.string.report_purpose_wholesale_liquidation),
            purposeOther = getString(Res.string.report_purpose_other)
        )
    }
}