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
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.OutlinedButton
import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.presentation.util.formatGroupedAmount
import luxeval.shared.generated.resources.ic_appraisal_detail
import luxeval.shared.generated.resources.ic_client_info
import luxeval.shared.generated.resources.ic_other_jewelry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppraisalDetailScreen(
    viewModel: AppraisalDetailViewModel,
    onBack: () -> Unit,
    onAddItem: (category: ItemCategory) -> Unit,
    onEditItem: (com.example.luxeval.domain.AppraisalItem) -> Unit,   // new
    onPreviewReport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCategoryPicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.appraisal?.documentId ?: "Appraisal") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_back), contentDescription = "Back")
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
                    onSave = viewModel::save
                )
            }

            item {
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
                                painter = painterResource(Res.drawable.ic_other_jewelry),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Jewelry Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    AssistChip(onClick = {}, label = { Text("${uiState.items.size} Items") })
                }
            }

            if (uiState.items.isEmpty()) {
                item { EmptyItemsState(onAddItem = { showCategoryPicker = true }) }
            } else {
                // where items are rendered, pass the click through:
                items(uiState.items, key = { it.id }) { appraisalItem ->
                    ItemRow(
                        item = appraisalItem,
                        onClick = { onEditItem(appraisalItem) },
                        onDelete = { viewModel.deleteItem(appraisalItem.id) }
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
                        Text("Add Another Item")
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Button(
                        onClick = onPreviewReport,
                        enabled = uiState.canFinalize,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Preview Report") }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)
                    ) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        if (uiState.appraisal?.status == AppraisalStatus.DRAFT) {
                            Button(
                                onClick = viewModel::markCompleted,
                                enabled = uiState.canFinalize,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.weight(1f)
                            ) { Text("Mark Completed") }
                        }
                    }
                }
                Spacer(Modifier.height(LuxeSpacing.xl))
            }
        }
    }

    if (showCategoryPicker) {
        CategoryPickerSheet(
            onCategorySelected = { category ->
                showCategoryPicker = false
                onAddItem(category)
            },
            onDismiss = { showCategoryPicker = false }
        )
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
                    text = "Appraisal Context",
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
                label = { Text("Internal Notes (Optional)") },
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
            value = current.label(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Primary Purpose") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
            AppraisalPurpose.entries.forEach { purpose ->
                DropdownMenuItem(
                    text = { Text(purpose.label()) },
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
    onSave: () -> Unit
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
                    text = "Client Information",
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
                label = { Text("Full Legal Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(LuxeSpacing.sm))
            OutlinedTextField(
                value = uiState.clientForm.email,
                onValueChange = onEmailChanged,
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(LuxeSpacing.sm))
            OutlinedTextField(
                value = uiState.clientForm.phone,
                onValueChange = onPhoneChanged,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            uiState.saveError?.let {
                Spacer(Modifier.height(LuxeSpacing.xs))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(LuxeSpacing.sm))
            Button(
                onClick = onSave,
                enabled = uiState.hasUnsavedChanges && !uiState.isSaving,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Save") }
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
        supportingContent = { Text(item.category.label()) },
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
        Text("No items added yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(LuxeSpacing.xs))
        Text(
            "Begin by adding the first piece of jewelry to this valuation report.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LuxeSpacing.md))
        Button(onClick = onAddItem) {
            Icon(painterResource(Res.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Add Jewelry Item")
        }
    }
}

private fun AppraisalPurpose.label(): String = when (this) {
    AppraisalPurpose.INSURANCE_REPLACEMENT -> "Insurance Replacement"
    AppraisalPurpose.FAIR_MARKET_VALUE -> "Fair Market Value"
    AppraisalPurpose.ESTATE_VALUATION -> "Estate Valuation"
    AppraisalPurpose.WHOLESALE_LIQUIDATION -> "Wholesale Liquidation"
    AppraisalPurpose.OTHER -> "Other"
}