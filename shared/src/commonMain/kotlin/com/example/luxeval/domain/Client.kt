package com.example.luxeval.domain

data class Client(
    val id: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)