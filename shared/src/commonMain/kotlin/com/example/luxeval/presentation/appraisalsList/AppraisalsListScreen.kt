package com.example.luxeval.presentation.appraisalsList


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import luxeval.shared.generated.resources.action_cancel
import luxeval.shared.generated.resources.action_go_to_profile
import luxeval.shared.generated.resources.action_new_appraisal
import luxeval.shared.generated.resources.appraisals_empty_body
import luxeval.shared.generated.resources.appraisals_empty_title
import luxeval.shared.generated.resources.appraisals_list_subtitle
import luxeval.shared.generated.resources.appraisals_list_title
import luxeval.shared.generated.resources.dialog_incomplete_profile_body
import luxeval.shared.generated.resources.dialog_incomplete_profile_title
import luxeval.shared.generated.resources.filter_all
import luxeval.shared.generated.resources.filter_archived
import luxeval.shared.generated.resources.filter_completed
import luxeval.shared.generated.resources.filter_drafts
import luxeval.shared.generated.resources.label_sort_by
import luxeval.shared.generated.resources.new_appraisal
import luxeval.shared.generated.resources.sort_client_name
import luxeval.shared.generated.resources.sort_date_added
import luxeval.shared.generated.resources.sort_estimated_value
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppraisalsListScreen(
    viewModel: AppraisalsListViewModel,
    onOpenAppraisal: (String) -> Unit,
    onNavigateToBusinessProfile: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showIncompleteProfileDialog by remember { mutableStateOf(false) }
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(topBar = { LuxeTopBar() }) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.onPullToRefresh() },
//            modifier = Modifier.fillMaxSize().padding(padding)
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(LuxeSpacing.md),
                verticalArrangement = Arrangement.spacedBy(LuxeSpacing.md)
            ) {
                item {
                    Column {
                        Text(stringResource(Res.string.appraisals_list_title), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            stringResource(Res.string.appraisals_list_subtitle),
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
                            Text(stringResource(Res.string.new_appraisal))
                        }
                    }
                }

                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)
                    ) {
                        AppraisalFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = uiState.filter == filter,
                                onClick = { viewModel.onFilterChanged(filter) },
                                label = { Text(stringResource(filter.label())) },
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
                        Text(stringResource(Res.string.label_sort_by), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    }
    if (showIncompleteProfileDialog) {
        AlertDialog(
            onDismissRequest = { showIncompleteProfileDialog = false },
            title = { Text(stringResource(Res.string.dialog_incomplete_profile_title)) },
            text = { Text(stringResource(Res.string.dialog_incomplete_profile_body)) },
            confirmButton = {
                TextButton(onClick = {
                    showIncompleteProfileDialog = false
                    onNavigateToBusinessProfile() // pass this in from MainScreen, same as SettingsScreen already does
                }) { Text(stringResource(Res.string.action_go_to_profile)) }
            },
            dismissButton = { TextButton(onClick = { showIncompleteProfileDialog = false }) { Text(stringResource(Res.string.action_cancel)) } }
        )
    }
}

@Composable
private fun SortDropdown(current: AppraisalSort, onSelected: (AppraisalSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) { Text(stringResource(current.label())) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppraisalSort.entries.forEach { sort ->
                DropdownMenuItem(text = { Text(stringResource(sort.label())) }, onClick = { onSelected(sort); expanded = false })
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
        Text(stringResource(Res.string.appraisals_empty_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(LuxeSpacing.xs))
        Text(
            stringResource(Res.string.appraisals_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LuxeSpacing.md))
        Button(onClick = onCreate) { Text(stringResource(Res.string.action_new_appraisal)) }
    }
}

private fun AppraisalFilter.label() = when (this) {
    AppraisalFilter.ALL -> Res.string.filter_all
    AppraisalFilter.DRAFTS -> Res.string.filter_drafts
    AppraisalFilter.COMPLETED -> Res.string.filter_completed
    AppraisalFilter.ARCHIVED -> Res.string.filter_archived
}

private fun AppraisalSort.label() = when (this) {
    AppraisalSort.DATE_ADDED -> Res.string.sort_date_added
    AppraisalSort.ESTIMATED_VALUE -> Res.string.sort_estimated_value
    AppraisalSort.CLIENT_NAME -> Res.string.sort_client_name
}