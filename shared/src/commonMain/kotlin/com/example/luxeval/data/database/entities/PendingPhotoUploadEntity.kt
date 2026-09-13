package com.example.luxeval.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_photo_uploads")
data class PendingPhotoUploadEntity(
    @PrimaryKey val id: String,       // deterministic — see PendingPhotoUploadId below
    val itemId: String,
    val localPath: String,
    val attempts: Int = 0,
    val lastError: String? = null,
    val createdAt: Long
)