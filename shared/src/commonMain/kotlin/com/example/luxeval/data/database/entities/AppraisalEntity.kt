package com.example.luxeval.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "appraisals",
    indices = [
        Index(value = ["documentNumber"], unique = true)
    ],
    foreignKeys = [ForeignKey(
        entity = ClientEntity::class,
        parentColumns = ["id"],
        childColumns = ["clientId"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class AppraisalEntity(
    @PrimaryKey val id: String,
    val documentId: String,
    val clientId: String?,
    val title: String? = null,
    val purpose: String,
    val status: String,       // AppraisalStatus.name
    val internalNotes: String? = null,
    val appraisalDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)