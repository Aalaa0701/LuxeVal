package com.example.luxeval.data.repository

import com.example.luxeval.data.pricing.MetalPriceApi
import com.example.luxeval.domain.MetalType

class PricingRepository(private val api: MetalPriceApi) {

    /** Rough estimate: spot price * net weight * a purity multiplier.
     *  This is a starting point for the appraiser, not a legal valuation —
     *  make that explicit in the UI copy. */
    suspend fun estimateMetalValue(
        metalType: MetalType,
        purity: String,
        netWeightGrams: Double
    ): Result<Double> = runCatching {
        val symbol = metalType.toApiSymbol() ?: error("No live pricing for $metalType")
        val spot = api.getSpotPrice(symbol)
        val purityFactor = purity.toPurityFactor()
        spot.pricePerGram * netWeightGrams * purityFactor
    }

    private fun MetalType.toApiSymbol(): String? = when (this) {
        MetalType.GOLD -> "XAU"
        MetalType.SILVER -> "XAG"
        MetalType.PLATINUM -> "XPT"
        MetalType.PALLADIUM -> "XPD"
        else -> null
    }

    private fun String.toPurityFactor(): Double = when {
        contains("24") -> 1.0
        contains("22") -> 22.0 / 24.0
        contains("21") -> 21.0 / 24.0
        contains("18") -> 18.0 / 24.0
        contains("14") -> 14.0 / 24.0
        contains("10") -> 10.0 / 24.0

        contains("950") -> 0.950
        contains("925") -> 0.925
        contains("900") -> 0.900

        else -> error("Unknown purity: $this")
    }
}