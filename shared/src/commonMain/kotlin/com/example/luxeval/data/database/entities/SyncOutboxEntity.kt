package com.example.luxeval.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_outbox")
data class SyncOutboxEntity(
    @PrimaryKey val id: String,           // deterministic, e.g. "appraisal-upsert-<appraisalId>"
    val entityType: String,               // "client" | "appraisal" | "item"
    val entityId: String,
    val operation: String,                // "upsert" | "delete"
    val attempts: Int = 0,
    val lastError: String? = null,
    val createdAt: Long
)