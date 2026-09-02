package com.example.luxeval.data.pricing

import com.example.luxeval.config.LuxValConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class MetalSpotPriceDto(
    val metal: String,      // "XAU", "XAG", "XPT"
    val currency: String,   // "USD"
    val pricePerGram: Double,
    val asOf: Long
)
@Serializable
data class MetalPriceResponse(
    val success: Boolean,
    val base: String,
    val timestamp: Long,
    val rates: Map<String, Double>
)

class MetalPriceApi(private val client: HttpClient) {
    // swap this base URL for whatever spot-price provider you pick
    // (metals-api.com, goldapi.io, etc. — all have a free tier)
    suspend fun getSpotPrice(
        metal: String,
        currency: String = "USD"
    ): MetalSpotPriceDto {
        val response: MetalPriceResponse = client.get(
            "https://api.metalpriceapi.com/v1/latest"
        ) {
            parameter("api_key", LuxValConfig.METAL_PRICE_API_KEY)
            parameter("base", currency)
            parameter("currencies", metal)
        }.body()

        val pricePerTroyOunce = response.rates["${currency}${metal}"]
            ?: error("No price found for $metal")

        val pricePerGram = pricePerTroyOunce / 31.1034768

        return MetalSpotPriceDto(
            metal = metal,
            currency = currency,
            pricePerGram = pricePerGram,
            asOf = response.timestamp
        )
    }
}