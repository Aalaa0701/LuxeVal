package com.example.luxeval.presentation.appraisalsList


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.LuxeTopBar
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_add
import luxeval.shared.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource
import com.example.luxeval.presentation.util.AppraisalCardView

@Composable
fun AppraisalsListScreen(
    viewModel: AppraisalsListViewModel,
    onOpenAppraisal: (String) -> Unit,
    onNavigateToBusinessProfile: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showIncompleteProfileDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { LuxeTopBar() }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.md)
        ) {
            item {
                Column {
                    Text("Appraisals", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Manage and review your valuation portfolio.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = {
                            viewModel.createDraftAndGetId(
                                onCreated = onOpenAppraisal,
                                onProfileIncomplete = { showIncompleteProfileDialog = true }
                            )
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(6.dp))
                    {
                        Icon(painterResource(Res.drawable.ic_add), contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("New Appraisal")
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    AppraisalFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = uiState.filter == filter,
                            onClick = { viewModel.onFilterChanged(filter) },
                            label = { Text(filter.label()) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sort by", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    SortDropdown(current = uiState.sort, onSelected = viewModel::onSortChanged)
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(LuxeSpacing.xl), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.cards.isEmpty()) {
                item {
                    EmptyAppraisalsState(onCreate = {
                        viewModel.createDraftAndGetId(
                            onCreated = onOpenAppraisal,
                            onProfileIncomplete = { showIncompleteProfileDialog = true }
                        )
                    })
                }
            } else {
                items(uiState.cards, key = { it.appraisal.id }) { card ->
                    AppraisalCardView(card = card, onClick = { onOpenAppraisal(card.appraisal.id) })
                }
            }
        }
    }
    if (showIncompleteProfileDialog) {
        AlertDialog(
            onDismissRequest = { showIncompleteProfileDialog = false },
            title = { Text("Complete Your Business Profile") },
            text = { Text("Add your business name, license number, logo, and signature before creating an appraisal — these appear on every generated report.") },
            confirmButton = {
                TextButton(onClick = {
                    showIncompleteProfileDialog = false
                    onNavigateToBusinessProfile() // pass this in from MainScreen, same as SettingsScreen already does
                }) { Text("Go to Profile") }
            },
            dismissButton = { TextButton(onClick = { showIncompleteProfileDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SortDropdown(current: AppraisalSort, onSelected: (AppraisalSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) { Text(current.label()) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppraisalSort.entries.forEach { sort ->
                DropdownMenuItem(text = { Text(sort.label()) }, onClick = { onSelected(sort); expanded = false })
            }
        }
    }
}

@Composable
private fun EmptyAppraisalsState(onCreate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = LuxeSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No appraisals yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(LuxeSpacing.xs))
        Text(
            "Start your first valuation report.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LuxeSpacing.md))
        Button(onClick = onCreate) { Text("+ New Appraisal") }
    }
}

private fun AppraisalFilter.label() = when (this) {
    AppraisalFilter.ALL -> "All"
    AppraisalFilter.DRAFTS -> "Drafts"
    AppraisalFilter.COMPLETED -> "Completed"
    AppraisalFilter.ARCHIVED -> "Archived"
}

private fun AppraisalSort.label() = when (this) {
    AppraisalSort.DATE_ADDED -> "Date Added"
    AppraisalSort.ESTIMATED_VALUE -> "Estimated Value"
    AppraisalSort.CLIENT_NAME -> "Client Name"
}