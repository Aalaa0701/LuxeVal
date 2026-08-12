package com.example.luxeval.data.mappers

import com.example.luxeval.data.database.entities.AppraisalEntity
import com.example.luxeval.domain.Appraisal
import com.example.luxeval.domain.AppraisalStatus

fun Appraisal.toEntity() = AppraisalEntity(
    id = id, clientId = clientId, title = title,
    status = status.name, appraisalDate = appraisalDate,
    createdAt = createdAt, updatedAt = updatedAt
)

fun AppraisalEntity.toDomain() = Appraisal(
    id = id, clientId = clientId, title = title,
    status = AppraisalStatus.valueOf(status),
    appraisalDate = appraisalDate,
    createdAt = createdAt, updatedAt = updatedAt
)