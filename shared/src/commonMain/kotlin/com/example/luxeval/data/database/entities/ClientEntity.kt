package com.example.luxeval.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val phone: String?,

    val email: String?,

    val address: String?
)