package com.example.luxeval.presentation.util


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.luxeval.domain.AppraisalPurpose
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_arrow_forward
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppraisalCardView(card: AppraisalCard, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Box {
            // Photo placeholder — swap for AsyncImage(card.representativeItem?.primaryPhotoUri) once image loading is wired.
            // replace the placeholder Box inside the outer Box { ... } with:
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                card.representativeItem?.primaryPhotoUri?.let { photoUri ->
                    PhotoImage(
                        photoUri = photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            StatusPill(
                status = card.appraisal.status,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(card.appraisal.documentId, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatDate(card.appraisal.appraisalDate), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = card.clientName ?: "No client yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(card.appraisal.purpose.displayName(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Est. Value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val hasItems = card.representativeItem != null
                    Text(
                        text = if (hasItems) formatCurrency(card.totalValue, card.representativeItem?.currency ?: "USD") else "Pending",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (hasItems) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onClick, modifier = Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.background)) {
                    Icon(painterResource(Res.drawable.ic_arrow_forward), contentDescription = "Open")
                }
            }
        }
    }
}


private fun AppraisalPurpose.displayName(): String = when (this) {
    AppraisalPurpose.INSURANCE_REPLACEMENT -> "Insurance Replacement"
    AppraisalPurpose.FAIR_MARKET_VALUE -> "Fair Market Value"
    AppraisalPurpose.ESTATE_VALUATION -> "Estate Valuation"
    AppraisalPurpose.WHOLESALE_LIQUIDATION -> "Wholesale Liquidation"
    AppraisalPurpose.OTHER -> "Other"
}