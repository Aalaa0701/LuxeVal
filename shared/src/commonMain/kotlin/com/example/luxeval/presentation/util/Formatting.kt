package com.example.luxeval.presentation.util

import com.example.luxeval.domain.MeasurementUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

/**
 * Single source of truth for currency/date formatting across the app — cards,
 * report preview, and the exported PDF's HTML all shared 90%-identical copies
 * of this before. One bug worth calling out: the old grouping logic
 * (`.reversed().chunked(3).joinToString(",").reversed()`) mangled negative
 * numbers, since the '-' sign got reversed into the wrong position. Fixed here.
 */

private val currencySymbols = mapOf(
    "USD" to "$", "EUR" to "€", "GBP" to "£", "CAD" to "$", "AUD" to "$", "EGP" to "E£"
)
fun lengthUnitLabel(unit: MeasurementUnit): String = if (unit == MeasurementUnit.IMPERIAL) "in" else "mm"
fun caratUnitLabel(unit: MeasurementUnit): String = if (unit == MeasurementUnit.IMPERIAL) "pt" else "ct"
/** Groups a whole-number value with thousands separators, e.g. 1234567 -> "1,234,567".
 *  Handles negatives correctly (sign is applied after grouping, not reversed with it). */
fun formatGroupedAmount(value: Long): String {
    val sign = if (value < 0) "-" else ""
    return sign + value.absoluteValue.toString().reversed().chunked(3).joinToString(",").reversed()
}

/** Appraisal values are always displayed as whole units in this app, so this rounds
 *  to the nearest whole number (previously some call sites used `.toLong()`, which
 *  truncates instead of rounds — e.g. 199.9 showed as 199, not 200). */
fun formatGroupedAmount(value: Double): String = formatGroupedAmount(value.roundToLong())

/** Renders a monetary value with its currency's symbol. Falls back to "CODE " for
 *  codes without a mapped symbol (e.g. "JPY 500") rather than silently guessing. */
fun formatCurrency(value: Double, currencyCode: String): String {
    val symbol = currencySymbols[currencyCode] ?: "$currencyCode "
    return "$symbol${formatGroupedAmount(value)}"
}

/** "Jan 5, 2026" style — used on appraisal cards, the report preview screen,
 *  and the exported PDF's HTML (via ReportHtmlBuilder). */
fun formatDate(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "$month ${dt.dayOfMonth}, ${dt.year}"
}