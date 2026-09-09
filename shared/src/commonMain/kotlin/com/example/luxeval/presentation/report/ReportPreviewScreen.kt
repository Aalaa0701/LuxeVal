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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.util.Logger
import com.example.luxeval.domain.AppraisalPurpose
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.theme.StatusColors
import com.example.luxeval.presentation.util.ErrorDialog
import com.example.luxeval.presentation.util.PhotoImage
import com.example.luxeval.presentation.util.formatCurrency
import com.example.luxeval.presentation.util.formatDate
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_back
import luxeval.shared.generated.resources.report_appraiser_signature
import luxeval.shared.generated.resources.report_business_logo
import luxeval.shared.generated.resources.report_certified_appraiser
import luxeval.shared.generated.resources.report_disclaimer
import luxeval.shared.generated.resources.report_estimated_value
import luxeval.shared.generated.resources.report_export_pdf
import luxeval.shared.generated.resources.report_itemized_valuation
import luxeval.shared.generated.resources.report_license
import luxeval.shared.generated.resources.report_notes
import luxeval.shared.generated.resources.report_partial_total_note
import luxeval.shared.generated.resources.report_pending_items
import luxeval.shared.generated.resources.report_preview_title
import luxeval.shared.generated.resources.report_purpose_insurance_replacement
import luxeval.shared.generated.resources.report_purpose_fair_market_value
import luxeval.shared.generated.resources.report_purpose_estate_valuation
import luxeval.shared.generated.resources.report_purpose_wholesale_liquidation
import luxeval.shared.generated.resources.report_purpose_other
import luxeval.shared.generated.resources.report_total_appraised_value
import luxeval.shared.generated.resources.report_business_name_not_set
import luxeval.shared.generated.resources.report_appraiser_name
import luxeval.shared.generated.resources.report_document_id
import luxeval.shared.generated.resources.report_date
import luxeval.shared.generated.resources.report_purpose
import luxeval.shared.generated.resources.report_client
import luxeval.shared.generated.resources.report_email
import luxeval.shared.generated.resources.report_phone
import luxeval.shared.generated.resources.report_unpriced_items_notice
import luxeval.shared.generated.resources.back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.report_preview_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.back),
                            modifier = Modifier.scale(
                                scaleX = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f,
                                scaleY = 1f
                            )
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.exportPdf(
                                onExported = { path ->
                                    viewModel.shareFile(path)
                                },
                                onError = {
                                    Logger.Level.Debug.name
                                }
                            )
                        },
                        enabled = !uiState.isExporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(Res.string.report_export_pdf))
                        }
                    }
                }
            )
        }
    ) { padding ->

        if (uiState.isLoading || uiState.appraisal == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.lg)
        ) {
            item {
                LetterheadSection(uiState.business)
            }

            item {
                ReportMetaSection(uiState)
            }

            item {
                Text(
                    text = stringResource(Res.string.report_itemized_valuation),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                uiState.items,
                key = { it.item.id }
            ) { row ->
                ItemReportCard(
                    row = row,
                    currencyCode = uiState.currencyCode
                )
            }

            if (uiState.hasUnpricedItems) {
                item {
                    Card(
                        shape = LuxeShapes.md,
                        colors = CardDefaults.cardColors(
                            containerColor = StatusColors.DraftBg
                        )
                    ) {
                        Text(
                            text = stringResource(
                                Res.string.report_unpriced_items_notice,
                                uiState.unpricedItemCount
                            ),
                            modifier = Modifier.padding(LuxeSpacing.md),
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusColors.DraftFg
                        )
                    }
                }
            }

            item {
                TotalValueSection(
                    total = uiState.totalValue,
                    currencyCode = uiState.currencyCode,
                    hasUnpriced = uiState.hasUnpricedItems,
                    unpricedCount = uiState.unpricedItemCount
                )
            }

            item {
                SignatureSection(uiState.business)
            }
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
            Text(
                text = stringResource(Res.string.report_total_appraised_value),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(
                modifier = Modifier.height(LuxeSpacing.sm)
            )

            Text(
                text = formatCurrency(total, currencyCode),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )

            if (hasUnpriced) {
                Spacer(
                    modifier = Modifier.height(LuxeSpacing.sm)
                )

                Text(
                    text = stringResource(
                        Res.string.report_pending_items,
                        unpricedCount
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = stringResource(
                        Res.string.report_partial_total_note
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.7f
                    )
                )
            }
        }
    }
}

@Composable
private fun LetterheadSection(
    business: ReportBusinessInfo
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        business.logoPath?.let { path ->
            PhotoImage(
                photoUri = path,
                contentDescription = stringResource(
                    Res.string.report_business_logo
                ),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(
                Modifier.height(LuxeSpacing.sm)
            )
        }

        Text(
            text = business.businessName.ifBlank {
                stringResource(
                    Res.string.report_business_name_not_set
                )
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (business.licenseNumber.isNotBlank()) {
            Text(
                text = stringResource(
                    Res.string.report_license,
                    business.licenseNumber
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (business.businessAddress.isNotBlank()) {
            Text(
                text = business.businessAddress,
                style = MaterialTheme.typography.bodySmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        if (business.businessPhone.isNotBlank()) {
            Text(
                text = business.businessPhone,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            Modifier.height(LuxeSpacing.sm)
        )

        HorizontalDivider()
    }
}

@Composable
private fun ReportMetaSection(
    uiState: ReportPreviewUiState
) {
    val appraisal = uiState.appraisal!!

    Card(
        shape = LuxeShapes.md,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            Modifier.padding(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)
        ) {
            MetaRow(
                label = stringResource(Res.string.report_document_id),
                value = appraisal.documentId
            )

            MetaRow(
                label = stringResource(Res.string.report_date),
                value = formatDate(appraisal.appraisalDate)
            )

            MetaRow(
                label = stringResource(Res.string.report_purpose),
                value = appraisal.purpose.reportLabel()
            )

            HorizontalDivider(
                Modifier.padding(
                    vertical = LuxeSpacing.xs
                )
            )

            MetaRow(
                label = stringResource(Res.string.report_client),
                value = uiState.client?.name
                    ?: "—"
            )

            uiState.client?.email?.let {
                MetaRow(
                    label = stringResource(Res.string.report_email),
                    value = it
                )
            }

            uiState.client?.phone?.let {
                MetaRow(
                    label = stringResource(Res.string.report_phone),
                    value = it
                )
            }
        }
    }
}

@Composable
private fun MetaRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ItemReportCard(
    row: ReportItemRow,
    currencyCode: String
) {
    Card(
        shape = LuxeShapes.md,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            Modifier.padding(LuxeSpacing.md)
        ) {
            if (row.item.photoUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        LuxeSpacing.xs
                    )
                ) {
                    items(row.item.photoUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(
                                    RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }

                Spacer(
                    Modifier.height(LuxeSpacing.sm)
                )
            }

            Text(
                text = row.item.itemName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = row.item.category.name
                    .lowercase()
                    .replaceFirstChar(Char::uppercase),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                Modifier.height(LuxeSpacing.sm)
            )

            row.specs.forEach { spec ->
                MetaRow(
                    label = spec.label,
                    value = spec.value
                )
            }

            row.item.notes?.let {
                Spacer(
                    Modifier.height(LuxeSpacing.xs)
                )

                Text(
                    text = stringResource(
                        Res.string.report_notes,
                        it
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            row.item.estimatedValue?.let {
                Spacer(
                    Modifier.height(LuxeSpacing.sm)
                )

                HorizontalDivider()

                Spacer(
                    Modifier.height(LuxeSpacing.xs)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            Res.string.report_estimated_value
                        ),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatCurrency(
                            it,
                            currencyCode
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SignatureSection(
    business: ReportBusinessInfo
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = LuxeSpacing.lg)
    ) {
        HorizontalDivider()

        Spacer(
            Modifier.height(LuxeSpacing.sm)
        )

        business.signaturePath?.let { path ->
            AsyncImage(
                model = path,
                contentDescription = stringResource(
                    Res.string.report_appraiser_signature
                ),
                modifier = Modifier.height(48.dp)
            )

            Spacer(
                Modifier.height(LuxeSpacing.xs)
            )
        }

        Text(
            text = business.businessName.ifBlank {
                stringResource(
                    Res.string.report_appraiser_name
                )
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = stringResource(
                Res.string.report_certified_appraiser
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(
                Res.string.report_disclaimer
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )
    }
}

@Composable
private fun AppraisalPurpose.reportLabel(): String =
    when (this) {
        AppraisalPurpose.INSURANCE_REPLACEMENT ->
            stringResource(
                Res.string.report_purpose_insurance_replacement
            )

        AppraisalPurpose.FAIR_MARKET_VALUE ->
            stringResource(
                Res.string.report_purpose_fair_market_value
            )

        AppraisalPurpose.ESTATE_VALUATION ->
            stringResource(
                Res.string.report_purpose_estate_valuation
            )

        AppraisalPurpose.WHOLESALE_LIQUIDATION ->
            stringResource(
                Res.string.report_purpose_wholesale_liquidation
            )

        AppraisalPurpose.OTHER ->
            stringResource(
                Res.string.report_purpose_other
            )
    }