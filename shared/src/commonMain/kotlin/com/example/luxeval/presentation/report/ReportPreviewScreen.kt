package com.example.luxeval.presentation.report


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.util.Logger
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.theme.StatusColors
import com.example.luxeval.presentation.util.ErrorDialog
import com.example.luxeval.presentation.util.PhotoImage
import com.example.luxeval.presentation.util.formatCurrency
import com.example.luxeval.presentation.util.formatDate
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
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    if (uiState.exportError != null) {
        ErrorDialog(
            error = uiState.exportError,
            onDismiss = viewModel::dismissExportError,
            onRetry = {
                viewModel.exportPdf(
                    onExported = { path ->
                        viewModel.shareFile(path)
                    },
                    onError = {
                        // ViewModel should update exportError
                    }
                )
            }
        )
    }
//    LaunchedEffect(uiState.exportError) {
//        uiState.exportError?.let { message ->
//            ErrorDialog(error = message, onDismiss = viewModel::dismissExportError, onRetry = { viewModel.exportPdf({},{}) })
////            snackbarHostState.showSnackbar("Export failed: $message")
//            viewModel.dismissExportError()
//        }
//    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Report Preview") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = "Back") } },
                actions = {
                    TextButton(onClick = {
                        viewModel.exportPdf(
                            onExported = { path -> viewModel.shareFile(path) },
                            onError = { error -> Logger.Level.Debug.name/*exportErrorMessage = error*/ }
                        )
                    }, enabled = !uiState.isExporting) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Export PDF")
                        }
                    }
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
            if (uiState.hasUnpricedItems) {
                item {
                    Card(shape = LuxeShapes.md, colors = CardDefaults.cardColors(containerColor = StatusColors.DraftBg)) {
                        Text(
                            "${uiState.unpricedItemCount} item(s) don't have a value yet. The total below reflects only priced items.",
                            modifier = Modifier.padding(LuxeSpacing.md),
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusColors.DraftFg
                        )
                    }
                }
            }
            item { TotalValueSection(uiState.totalValue, uiState.currencyCode, uiState.hasUnpricedItems, uiState.unpricedItemCount) }

//            item { TotalValueSection(uiState.totalValue, uiState.currencyCode) }
            item { SignatureSection(uiState.business) }
        }
    }
}
@Composable
private fun TotalValueSection(
    total: Double,
    currencyCode: String,
    hasUnpriced: Boolean,
    unpricedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = LuxeShapes.md,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = LuxeSpacing.md,
                    vertical = LuxeSpacing.lg
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Title
            Text(
                text = "TOTAL APPRAISED VALUE",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Main value
            Spacer(modifier = Modifier.height(LuxeSpacing.sm))

            Text(
                text = formatCurrency(total, currencyCode),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )

            // Pending items information
            if (hasUnpriced) {
                Spacer(modifier = Modifier.height(LuxeSpacing.sm))

                Text(
                    text = "$unpricedCount item(s) pending",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Partial total — excludes items without a value",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.7f
                    )
                )
            }
        }
    }
}
//@Composable
//private fun TotalValueSection(total: Double, currencyCode: String, hasUnpriced: Boolean, unpricedCount: Int) {
//    Card(shape = LuxeShapes.md, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(LuxeSpacing.md),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                if (hasUnpriced) "Total Appraised Value (Partial — $unpricedCount pending)" else "Total Appraised Value",
//                style = MaterialTheme.typography.titleMedium,
//                color = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//            Text(
//                formatCurrency(total, currencyCode),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onPrimaryContainer
//            )
//        }
//    }
//}
@Composable
private fun LetterheadSection(business: ReportBusinessInfo) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        business.logoPath?.let { path ->
            PhotoImage(
                photoUri = path,
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
        Text(
            text = "This appraisal represents the professional opinion of the undersigned appraiser as of the date shown above, based on visual inspection and the information provided. It is not a guarantee of sale price, insurance payout, or future value, and market conditions may cause actual value to differ. This report is intended solely for the stated purpose and named client and should not be relied upon by any other party.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

    }
}



private fun com.example.luxeval.domain.AppraisalPurpose.reportLabel(): String = when (this) {
    com.example.luxeval.domain.AppraisalPurpose.INSURANCE_REPLACEMENT -> "Insurance Replacement"
    com.example.luxeval.domain.AppraisalPurpose.FAIR_MARKET_VALUE -> "Fair Market Value"
    com.example.luxeval.domain.AppraisalPurpose.ESTATE_VALUATION -> "Estate Valuation"
    com.example.luxeval.domain.AppraisalPurpose.WHOLESALE_LIQUIDATION -> "Wholesale Liquidation"
    com.example.luxeval.domain.AppraisalPurpose.OTHER -> "Other"
}