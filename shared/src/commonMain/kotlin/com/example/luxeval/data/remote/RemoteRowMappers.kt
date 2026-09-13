package com.example.luxeval.data.remote


import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalItem
import com.example.luxeval.domain.Client

fun Client.toRemoteRow(userId: String) = RemoteClient(
    id = id,
    name = name,
    phone = phone,
    email = email,
    address = address,
    userId = userId
)

fun Appraisal.toRemoteRow(userId: String) = RemoteAppraisal(
    id = id,
    documentId = documentId,
    clientId = clientId,
    title = title,
    purpose = purpose.name,
    status = status.name,
    internalNotes = internalNotes,
    appraisalDate = appraisalDate,
    createdAt = createdAt,
    updatedAt = updatedAt,
    userId = userId
)

//fun AppraisalItem.toRemoteRow(userId: String) = RemoteItem(
//    id = id,
//    appraisalId = appraisalId,
//    itemName = itemName,
//    category = category.name,
//    photoUris = photoUris.joinToString("‖"),
//    condition = condition.name,
//    conditionNotes = conditionNotes,
//    grossWeightGrams = grossWeightGrams,
//    hallmarks = hallmarks,
//    estimatedValue = estimatedValue,
//    currency = currency,
//    valuationBasis = valuationBasis.name,
//    valueSource = valueSource.name,
//    notes = notes,
//    createdAt = createdAt,
//    updatedAt = updatedAt,
//    details = detailsToJson(),
//    userId = userId
//)
fun AppraisalItem.toRemoteRow(userId: String) = RemoteItem(
    id = id,
    appraisalId = appraisalId,
    itemName = itemName,
    category = category.name,
    photoUris = photoUris.filter { it.startsWith("supabase://") }.joinToString("‖"),
    condition = condition.name,
    conditionNotes = conditionNotes,
    grossWeightGrams = grossWeightGrams,
    hallmarks = hallmarks,
    estimatedValue = estimatedValue,
    currency = currency,
    valuationBasis = valuationBasis.name,
    valueSource = valueSource.name,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    details = detailsToJson(),
    userId = userId
)