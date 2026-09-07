package com.example.luxeval.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RemoteClient(
    val id: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)

@Serializable
data class RemoteAppraisal(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("document_id") val documentId: String,
    @SerialName("client_id") val clientId: String? = null,
    val title: String? = null,
    val purpose: String,
    val status: String,
    @SerialName("internal_notes") val internalNotes: String? = null,
    @SerialName("appraisal_date") val appraisalDate: Long,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long
)
@Serializable
data class RemoteBusinessProfile(
    @SerialName("user_id") val userId: String,
    @SerialName("business_name") val businessName: String? = null,
    @SerialName("license_number") val licenseNumber: String? = null,
    @SerialName("business_address") val businessAddress: String? = null,
    @SerialName("business_phone") val businessPhone: String? = null,
    @SerialName("logo_uri") val logoUri: String? = null,
    @SerialName("signature_uri") val signatureUri: String? = null,
    @SerialName("currency_code") val currencyCode: String? = null,
    @SerialName("measurement_unit") val measurementUnit: String? = null,
    @SerialName("updated_at") val updatedAt: Long
)

@Serializable
data class RemoteItem(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("appraisal_id") val appraisalId: String,
    @SerialName("item_name") val itemName: String,
    val category: String,
    @SerialName("photo_uris") val photoUris: String,
    val condition: String,
    @SerialName("condition_notes") val conditionNotes: String? = null,
    @SerialName("gross_weight_grams") val grossWeightGrams: Double? = null,
    val hallmarks: String? = null,
    @SerialName("estimated_value") val estimatedValue: Double? = null,
    val currency: String,
    @SerialName("valuation_basis") val valuationBasis: String,
    @SerialName("value_source") val valueSource: String,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    val details: JsonElement
)