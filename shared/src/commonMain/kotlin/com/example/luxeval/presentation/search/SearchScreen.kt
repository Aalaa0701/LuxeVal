package com.example.luxeval.presentation.search

import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.LuxeTopBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.util.AppraisalCardView
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_search
import luxeval.shared.generated.resources.ic_search_off
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onOpenAppraisal: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { LuxeTopBar() }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.md)
        ) {
            item {
                Text("Global Search", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            }

            item {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search appraisals, clients, or jewelry items...") },
                    leadingIcon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null, Modifier.size(24.dp)) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            when {
                !uiState.hasSearched -> item {
                    Text(
                        "Start typing to search across your appraisals and clients.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = LuxeSpacing.lg)
                    )
                }

                !uiState.hasResults -> item {
                    NoResultsState(query = uiState.query, onClear = viewModel::clearSearch)
                }

                else -> {
                    if (uiState.appraisalResults.isNotEmpty()) {
                        item {
                            Text(
                                "Appraisals · ${uiState.appraisalResults.size} Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(uiState.appraisalResults, key = { it.appraisal.id }) { card ->
                            AppraisalCardView(card = card, onClick = { onOpenAppraisal(card.appraisal.id) })
                        }
                    }

                    if (uiState.clientResults.isNotEmpty()) {
                        item {
                            Text(
                                "Customers · ${uiState.clientResults.size} Result${if (uiState.clientResults.size == 1) "" else "s"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(uiState.clientResults, key = { it.id }) { client ->
                            ListItem(
                                headlineContent = { Text(client.name) },
                                supportingContent = { Text(client.email ?: client.phone.orEmpty()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoResultsState(query: String, onClear: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = LuxeSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painterResource(Res.drawable.ic_search_off),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LuxeSpacing.sm))
        Text("No results found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(LuxeSpacing.xs))
        Text(
            "We couldn't find anything matching \"$query\". Try adjusting your search terms.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(LuxeSpacing.md))
        Button(onClick = onClear) { Text("Clear Search") }
    }
}