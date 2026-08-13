package com.example.luxeval.data.mappers

import com.example.luxeval.data.database.entities.AppraisalEntity
import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalPurpose
import com.example.luxeval.domain.AppraisalStatus

fun Appraisal.toEntity() = AppraisalEntity(
    id = id, documentId = documentId, clientId = clientId, title = title,
    purpose = purpose.name, status = status.name, appraisalDate = appraisalDate,
    createdAt = createdAt, updatedAt = updatedAt
)

fun AppraisalEntity.toDomain() = Appraisal(
    id = id, documentId = documentId, clientId = clientId, title = title,
    purpose = AppraisalPurpose.valueOf(purpose), status = AppraisalStatus.valueOf(status),
    appraisalDate = appraisalDate,
    createdAt = createdAt, updatedAt = updatedAt
)