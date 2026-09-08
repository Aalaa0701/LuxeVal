package com.example.luxeval.presentation.appraisalDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.domain.AppraisalPurpose
import com.example.luxeval.domain.ItemCategory
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_add
import luxeval.shared.generated.resources.ic_back
import luxeval.shared.generated.resources.ic_delete
import luxeval.shared.generated.resources.ic_more_vert
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.OutlinedButton
import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.presentation.theme.StatusColors
import com.example.luxeval.presentation.util.ErrorDialog
import com.example.luxeval.presentation.util.formatGroupedAmount
import luxeval.shared.generated.resources.action_add_another_item
import luxeval.shared.generated.resources.action_add_jewelry_item
import luxeval.shared.generated.resources.action_archive
import luxeval.shared.generated.resources.action_cancel
import luxeval.shared.generated.resources.action_delete
import luxeval.shared.generated.resources.action_delete_appraisal
import luxeval.shared.generated.resources.action_go_back
import luxeval.shared.generated.resources.action_mark_completed
import luxeval.shared.generated.resources.action_mark_completed_anyway
import luxeval.shared.generated.resources.action_preview_report
import luxeval.shared.generated.resources.action_restore_draft
import luxeval.shared.generated.resources.action_save
import luxeval.shared.generated.resources.appraisal_detail_fallback_title
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.delete_appraisal_confirm_body
import luxeval.shared.generated.resources.delete_item_confirm_body
import luxeval.shared.generated.resources.delete_item_title
import luxeval.shared.generated.resources.ic_appraisal_detail
import luxeval.shared.generated.resources.ic_client_info
import luxeval.shared.generated.resources.ic_other_jewelry
import luxeval.shared.generated.resources.items_count
import luxeval.shared.generated.resources.items_empty_body
import luxeval.shared.generated.resources.items_empty_title
import luxeval.shared.generated.resources.label_email
import luxeval.shared.generated.resources.label_full_legal_name
import luxeval.shared.generated.resources.label_phone_number
import luxeval.shared.generated.resources.label_primary_purpose
import luxeval.shared.generated.resources.mark_completed_unpriced_confirm
import luxeval.shared.generated.resources.more
import luxeval.shared.generated.resources.purpose_estate
import luxeval.shared.generated.resources.purpose_fair_market
import luxeval.shared.generated.resources.purpose_insurance
import luxeval.shared.generated.resources.purpose_other
import luxeval.shared.generated.resources.purpose_wholesale
import luxeval.shared.generated.resources.section_appraisal_context
import luxeval.shared.generated.resources.section_client_info
import luxeval.shared.generated.resources.section_jewelry_items
import luxeval.shared.generated.resources.unpriced_items_title
import luxeval.shared.generated.resources.this_item
import luxeval.shared.generated.resources.report_unpriced_items_notice
import luxeval.shared.generated.resources.internal_notes
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppraisalDetailScreen(
    viewModel: AppraisalDetailViewModel,
    onBack: () -> Unit,
    onAddItem: (category: ItemCategory) -> Unit,
    onEditItem: (com.example.luxeval.domain.AppraisalItem) -> Unit,
    onPreviewReport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteAppraisalConfirm by remember { mutableStateOf(false) }
    var showUnpricedConfirm by remember { mutableStateOf(false) }
    var pendingItemDeleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.appraisal?.documentId ?: stringResource(Res.string.appraisal_detail_fallback_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back))
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(painterResource(Res.drawable.ic_more_vert), contentDescription = stringResource(Res.string.more))
                        }
                        DropdownMenu(expanded = showOverflowMenu, onDismissRequest = { showOverflowMenu = false }) {
                            if (uiState.appraisal?.status == AppraisalStatus.ARCHIVED) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.action_restore_draft)) },
                                    onClick = { showOverflowMenu = false; viewModel.restoreFromArchive() }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.action_archive)) },
                                    onClick = { showOverflowMenu = false; viewModel.archiveAppraisal() }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.action_delete_appraisal), color = MaterialTheme.colorScheme.error) },
                                onClick = { showOverflowMenu = false; showDeleteAppraisalConfirm = true }
                            )
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
            item { AppraisalContextSection(uiState = uiState, onPurposeChanged = viewModel::onPurposeChanged, onNotesChanged = viewModel::onNotesChanged) }

            item {
                ClientInfoSection(
                    uiState = uiState,
                    onNameChanged = viewModel::onClientNameChanged,
                    onEmailChanged = viewModel::onClientEmailChanged,
                    onPhoneChanged = viewModel::onClientPhoneChanged,
                    onSave = viewModel::save,
                    onDismiss = viewModel::dismissSaveError
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        modifier = Modifier.size(36.dp), shape = LuxeShapes.sm,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(painter = painterResource(Res.drawable.ic_other_jewelry), contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(Res.string.section_jewelry_items), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.weight(1f))
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                stringResource(
                                Res.string.items_count,
                                uiState.items.size
                                )
                            )
                        }
                    )
                }
            }

            if (uiState.items.isEmpty()) {
                item { EmptyItemsState(onAddItem = { showCategoryPicker = true }) }
            } else {
                items(uiState.items, key = { it.id }) { appraisalItem ->
                    ItemRow(
                        item = appraisalItem,
                        onClick = { onEditItem(appraisalItem) },
                        onDelete = { pendingItemDeleteId = appraisalItem.id }
                    )
                }
                item {
                    OutlinedButton(
                        onClick = { showCategoryPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Icon(painterResource(Res.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(stringResource(Res.string.action_add_another_item))
                    }
                }
            }

            // NEW — unpriced items warning, shown right above the finalize actions
            if (uiState.hasUnpricedItems) {
                item { UnpricedItemsBanner(itemNames = uiState.unpricedItems.map { it.itemName }) }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Button(
                        onClick = onPreviewReport,
                        enabled = uiState.canFinalize,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(Res.string.action_preview_report)) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)
                    ) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text(stringResource(Res.string.action_cancel)) }
                        if (uiState.appraisal?.status == AppraisalStatus.DRAFT) {
                            Button(
                                onClick = {
                                    if (uiState.hasUnpricedItems) showUnpricedConfirm = true
                                    else viewModel.markCompleted()
                                },
                                enabled = uiState.canFinalize,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.weight(1f)
                            ) { Text(stringResource(Res.string.action_mark_completed), color = MaterialTheme.colorScheme.onSecondary) }
                        }
                    }
                }
                Spacer(Modifier.height(LuxeSpacing.xl))
            }
        }
    }

    if (showCategoryPicker) {
        CategoryPickerSheet(
            onCategorySelected = { category -> showCategoryPicker = false; onAddItem(category) },
            onDismiss = { showCategoryPicker = false }
        )
    }

    if (showDeleteAppraisalConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAppraisalConfirm = false },
            title = { Text(stringResource(Res.string.action_delete_appraisal)) },
            text = { Text(
                stringResource(
                    Res.string.delete_appraisal_confirm_body,
                    uiState.items.size
                )
            ) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAppraisalConfirm = false
                    viewModel.deleteAppraisal(onDeleted = onBack)
                }) { Text(stringResource(Res.string.action_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteAppraisalConfirm = false }) { Text(stringResource(Res.string.action_cancel)) } }
        )
    }

    if (showUnpricedConfirm) {
        AlertDialog(
            onDismissRequest = { showUnpricedConfirm = false },
            title = { Text(stringResource(Res.string.unpriced_items_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.mark_completed_unpriced_confirm,
                        uiState.unpricedItems.size,
                        uiState.unpricedItems.joinToString(", ") { it.itemName }
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { showUnpricedConfirm = false; viewModel.markCompleted() }) { Text(stringResource(Res.string.action_mark_completed_anyway)) }
            },
            dismissButton = { TextButton(onClick = { showUnpricedConfirm = false }) { Text(stringResource(Res.string.action_go_back)) } }
        )
    }

    pendingItemDeleteId?.let { itemId ->
        val itemName = uiState.items.firstOrNull { it.id == itemId }?.itemName ?: stringResource(Res.string.this_item)
        AlertDialog(
            onDismissRequest = { pendingItemDeleteId = null },
            title = { Text(stringResource(Res.string.delete_item_title)) },
            text = { Text(
                stringResource(
                    Res.string.delete_item_confirm_body,
                    itemName
                )
            ) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(itemId)
                    pendingItemDeleteId = null
                }) { Text(stringResource(Res.string.action_delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pendingItemDeleteId = null }) { Text(stringResource(Res.string.action_cancel)) } }
        )
    }
}

// NEW composable — sits above Preview/Mark Completed buttons
@Composable
private fun UnpricedItemsBanner(itemNames: List<String>) {
    Card(
        shape = LuxeShapes.md,
        colors = CardDefaults.cardColors(containerColor = StatusColors.DraftBg)
    ) {
        Column(Modifier.padding(LuxeSpacing.md)) {
            Text(
                stringResource(Res.string.unpriced_items_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = StatusColors.DraftFg
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(
                    Res.string.report_unpriced_items_notice,
                    itemNames.size,
                    itemNames.joinToString(", ")
                ),
                style = MaterialTheme.typography.bodySmall,
                color = StatusColors.DraftFg
            )
        }
    }
}

@Composable
private fun AppraisalContextSection(
    uiState: AppraisalDetailUiState,
    onPurposeChanged: (AppraisalPurpose) -> Unit,
    onNotesChanged: (String) -> Unit
) {
    Card(
        shape = LuxeShapes.lg,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(modifier = Modifier.padding(LuxeSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(36.dp),
                    shape = LuxeShapes.sm,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_appraisal_detail),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(Res.string.section_appraisal_context),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(LuxeSpacing.sm))
            PurposeDropdown(current = uiState.purpose, onSelected = onPurposeChanged)
            Spacer(Modifier.height(LuxeSpacing.sm))
            OutlinedTextField(
                value = uiState.internalNotes,
                onValueChange = onNotesChanged,
                label = { Text(stringResource(Res.string.internal_notes)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurposeDropdown(current: AppraisalPurpose, onSelected: (AppraisalPurpose) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = stringResource(current.label()),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(Res.string.label_primary_purpose)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
            AppraisalPurpose.entries.forEach { purpose ->
                DropdownMenuItem(
                    text = { Text(stringResource(purpose.label())) },
                    onClick = { onSelected(purpose); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun ClientInfoSection(
    uiState: AppraisalDetailUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        shape = LuxeShapes.lg,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(modifier = Modifier.padding(LuxeSpacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(36.dp),
                    shape = LuxeShapes.sm,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_client_info),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(Res.string.section_client_info),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
            Spacer(Modifier.height(LuxeSpacing.sm))
            OutlinedTextField(
                value = uiState.clientForm.name,
                onValueChange = onNameChanged,
                label = { Text(stringResource(Res.string.label_full_legal_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(LuxeSpacing.sm))
            OutlinedTextField(
                value = uiState.clientForm.email,
                onValueChange = onEmailChanged,
                label = { Text(stringResource(Res.string.label_email)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(LuxeSpacing.sm))
            OutlinedTextField(
                value = uiState.clientForm.phone,
                onValueChange = onPhoneChanged,
                label = { Text(stringResource(Res.string.label_phone_number)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            uiState.saveError?.let {
                Spacer(Modifier.height(LuxeSpacing.xs))
                ErrorDialog(error = uiState.saveError, onDismiss = onDismiss, onRetry =  onSave)
            }
            Spacer(Modifier.height(LuxeSpacing.sm))
            Button(
                onClick = onSave,
                enabled = uiState.hasUnsavedChanges && !uiState.isSaving,
                modifier = Modifier.align(Alignment.End)
            ) { Text(stringResource(Res.string.action_save)) }
        }
    }
}

@Composable
private fun ItemRow(item: com.example.luxeval.domain.AppraisalItem, onClick: () -> Unit, onDelete: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Card(
                modifier = Modifier.size(36.dp),
                shape = LuxeShapes.sm,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(item.category.icon()),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        headlineContent = { Text(item.itemName, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(stringResource(item.category.label())) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                item.estimatedValue?.let {
                    Text(
                        "${item.currency} ${formatGroupedAmount(it)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(painterResource(Res.drawable.ic_delete), contentDescription = "Delete item")
                }
            }
        }
    )
}
@Composable
private fun EmptyItemsState(onAddItem: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = LuxeSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Res.string.items_empty_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(LuxeSpacing.xs))
        Text(
            stringResource(Res.string.items_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LuxeSpacing.md))
        Button(onClick = onAddItem) {
            Icon(painterResource(Res.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(stringResource(Res.string.action_add_jewelry_item))
        }
    }
}

private fun AppraisalPurpose.label(): StringResource = when (this) {
    AppraisalPurpose.INSURANCE_REPLACEMENT -> Res.string.purpose_insurance
    AppraisalPurpose.FAIR_MARKET_VALUE -> Res.string.purpose_fair_market
    AppraisalPurpose.ESTATE_VALUATION -> Res.string.purpose_estate
    AppraisalPurpose.WHOLESALE_LIQUIDATION -> Res.string.purpose_wholesale
    AppraisalPurpose.OTHER -> Res.string.purpose_other
}