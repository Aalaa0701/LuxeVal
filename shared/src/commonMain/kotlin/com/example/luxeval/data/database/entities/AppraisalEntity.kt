package com.example.luxeval.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "appraisals",
    foreignKeys = [ForeignKey(
        entity = ClientEntity::class,
        parentColumns = ["id"],
        childColumns = ["clientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class AppraisalEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val title: String? = null,
    val status: String,       // AppraisalStatus.name
    val appraisalDate: Long,
    val createdAt: Long,
    val updatedAt: Long
)