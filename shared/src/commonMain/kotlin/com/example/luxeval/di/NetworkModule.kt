package com.example.luxeval.di

import com.example.luxeval.data.pricing.MetalPriceApi
import com.example.luxeval.data.repository.PricingRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) { json() }
            install(Logging) { level = LogLevel.INFO }
        }
    }
    single { MetalPriceApi(get()) }
    single { PricingRepository(get()) }
}