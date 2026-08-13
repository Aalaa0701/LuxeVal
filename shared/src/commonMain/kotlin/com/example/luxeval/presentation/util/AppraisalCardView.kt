package com.example.luxeval.presentation.util


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LuxeColors
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun AppraisalCardView(card: AppraisalCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxeColors.Surface)
    ) {
        Box {
            // Photo placeholder — swap for AsyncImage(card.representativeItem?.primaryPhotoUri) once image loading is wired.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(LuxeColors.NavyLight)
            )
            StatusPill(
                status = card.appraisal.status,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(card.appraisal.documentId, style = MaterialTheme.typography.bodyMedium, color = LuxeColors.OnSurfaceMuted)
                Text(formatDate(card.appraisal.appraisalDate), style = MaterialTheme.typography.bodyMedium, color = LuxeColors.OnSurfaceMuted)
            }
            Text(
                text = card.clientName ?: "No client yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(card.appraisal.purpose.displayName(), style = MaterialTheme.typography.bodyMedium, color = LuxeColors.OnSurfaceMuted)
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Est. Value", style = MaterialTheme.typography.labelSmall, color = LuxeColors.OnSurfaceMuted)
                    val value = card.representativeItem?.estimatedValue
                    Text(
                        text = if (value != null) formatCurrency(value, card.representativeItem.currency) else "Pending",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (value != null) LuxeColors.Olive else LuxeColors.OnSurfaceMuted
                    )
                }
                IconButton(onClick = onClick, modifier = Modifier.clip(RoundedCornerShape(50)).background(LuxeColors.Background)) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Open")
                }
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "$month ${dt.dayOfMonth}, ${dt.year}"
}

private fun formatCurrency(value: Double, currencyCode: String): String {
    val symbol = when (currencyCode) { "USD" -> "$"; "EUR" -> "€"; "GBP" -> "£"; else -> "$currencyCode " }
    return "$symbol${"%,.0f".format(value)}"
}

private fun com.example.luxeval.domain.AppraisalPurpose.displayName(): String = when (this) {
    com.example.luxeval.domain.AppraisalPurpose.INSURANCE_REPLACEMENT -> "Insurance Replacement"
    com.example.luxeval.domain.AppraisalPurpose.FAIR_MARKET_VALUE -> "Fair Market Value"
    com.example.luxeval.domain.AppraisalPurpose.ESTATE_VALUATION -> "Estate Valuation"
    com.example.luxeval.domain.AppraisalPurpose.WHOLESALE_LIQUIDATION -> "Wholesale Liquidation"
    com.example.luxeval.domain.AppraisalPurpose.OTHER -> "Other"
}