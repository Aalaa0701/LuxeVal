package com.example.luxeval.presentation.report


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPreviewScreen(
    viewModel: ReportPreviewViewModel,
    onBack: () -> Unit,
    onExportPdf: () -> Unit   // wired up in the next step
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Preview") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = "Back") } },
                actions = {
                    TextButton(onClick = onExportPdf) { Text("Export PDF") }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading || uiState.appraisal == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.lg)
        ) {
            item { LetterheadSection(uiState.business) }
            item { ReportMetaSection(uiState) }
            item {
                Text(
                    "Itemized Valuation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(uiState.items, key = { it.item.id }) { row ->
                ItemReportCard(row = row, currencyCode = uiState.currencyCode)
            }
            item { TotalValueSection(uiState.totalValue, uiState.currencyCode) }
            item { SignatureSection(uiState.business) }
        }
    }
}

@Composable
private fun LetterheadSection(business: ReportBusinessInfo) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        business.logoPath?.let { path ->
            AsyncImage(
                model = path,
                contentDescription = "Business logo",
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.height(LuxeSpacing.sm))
        }
        Text(
            business.businessName.ifBlank { "Business Name Not Set" },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        if (business.licenseNumber.isNotBlank()) {
            Text("License #${business.licenseNumber}", style = MaterialTheme.typography.bodySmall)
        }
        if (business.businessAddress.isNotBlank()) {
            Text(business.businessAddress, style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
        if (business.businessPhone.isNotBlank()) {
            Text(business.businessPhone, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(LuxeSpacing.sm))
        HorizontalDivider()
    }
}

@Composable
private fun ReportMetaSection(uiState: ReportPreviewUiState) {
    val appraisal = uiState.appraisal!!
    Card(shape = LuxeShapes.md, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(LuxeSpacing.md), verticalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)) {
            MetaRow("Document ID", appraisal.documentId)
            MetaRow("Date", formatDate(appraisal.appraisalDate))
            MetaRow("Purpose", appraisal.purpose.reportLabel())
            HorizontalDivider(Modifier.padding(vertical = LuxeSpacing.xs))
            MetaRow("Client", uiState.client?.name ?: "—")
            uiState.client?.email?.let { MetaRow("Email", it) }
            uiState.client?.phone?.let { MetaRow("Phone", it) }
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ItemReportCard(row: ReportItemRow, currencyCode: String) {
    Card(shape = LuxeShapes.md, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)) {
        Column(Modifier.padding(LuxeSpacing.md)) {
            if (row.item.photoUris.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)) {
                    items(row.item.photoUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
                Spacer(Modifier.height(LuxeSpacing.sm))
            }
            Text(row.item.itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(row.item.category.name.lowercase().replaceFirstChar(Char::uppercase), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(LuxeSpacing.sm))
            row.specs.forEach { spec ->
                MetaRow(spec.label, spec.value)
            }
            row.item.notes?.let {
                Spacer(Modifier.height(LuxeSpacing.xs))
                Text("Notes: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            row.item.estimatedValue?.let {
                Spacer(Modifier.height(LuxeSpacing.sm))
                HorizontalDivider()
                Spacer(Modifier.height(LuxeSpacing.xs))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Estimated Value", fontWeight = FontWeight.Bold)
                    Text(formatCurrency(it, currencyCode), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
private fun TotalValueSection(total: Double, currencyCode: String) {
    Card(shape = LuxeShapes.md, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(LuxeSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total Appraised Value", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                formatCurrency(total, currencyCode),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SignatureSection(business: ReportBusinessInfo) {
    Column(Modifier.fillMaxWidth().padding(top = LuxeSpacing.lg)) {
        HorizontalDivider()
        Spacer(Modifier.height(LuxeSpacing.sm))
        business.signaturePath?.let { path ->
            AsyncImage(
                model = path,
                contentDescription = "Appraiser signature",
                modifier = Modifier.height(48.dp)
            )
            Spacer(Modifier.height(LuxeSpacing.xs))
        }
        Text(business.businessName.ifBlank { "Appraiser" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text("Certified Appraiser", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatDate(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    return "$month ${dt.dayOfMonth}, ${dt.year}"
}

private fun formatCurrency(value: Double, currencyCode: String): String {
    val symbol = when (currencyCode) { "USD" -> "$"; "EUR" -> "€"; "GBP" -> "£"; else -> "$currencyCode " }
    val whole = value.toLong()
    val formatted = whole.toString().reversed().chunked(3).joinToString(",").reversed()
    return "$symbol$formatted"
}

private fun com.example.luxeval.domain.AppraisalPurpose.reportLabel(): String = when (this) {
    com.example.luxeval.domain.AppraisalPurpose.INSURANCE_REPLACEMENT -> "Insurance Replacement"
    com.example.luxeval.domain.AppraisalPurpose.FAIR_MARKET_VALUE -> "Fair Market Value"
    com.example.luxeval.domain.AppraisalPurpose.ESTATE_VALUATION -> "Estate Valuation"
    com.example.luxeval.domain.AppraisalPurpose.WHOLESALE_LIQUIDATION -> "Wholesale Liquidation"
    com.example.luxeval.domain.AppraisalPurpose.OTHER -> "Other"
}