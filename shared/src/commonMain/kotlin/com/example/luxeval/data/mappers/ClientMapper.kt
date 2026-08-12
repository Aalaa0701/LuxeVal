package com.example.luxeval.data.mappers

import com.example.luxeval.data.database.entities.ClientEntity
import com.example.luxeval.domain.Client

fun Client.toEntity(): ClientEntity =
    ClientEntity(
        id = id,
        name = name,
        phone = phone,
        email = email,
        address = address
    )

fun ClientEntity.toDomain(): Client =
    Client(
        id = id,
        name = name,
        phone = phone,
        email = email,
        address = address
    )