package com.example.luxeval.presentation.dashboard


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.util.AppraisalCardView
import com.example.luxeval.presentation.util.LuxeTopBar
import com.example.luxeval.presentation.theme.LuxeColors

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenAppraisal: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { LuxeTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.createDraftAndGetId(onOpenAppraisal) },
                containerColor = LuxeColors.Olive
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New Appraisal")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column {
                    Text("Good Morning", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Text(currentDateLabel(), style = MaterialTheme.typography.bodyMedium, color = LuxeColors.OnSurfaceMuted)
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total Appraisals", uiState.totalAppraisals.toString(), Modifier.weight(1f))
                    StatCard("Completed", uiState.completedCount.toString(), Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Drafts", uiState.draftsCount.toString(), Modifier.weight(1f), valueColor = LuxeColors.Olive)
                    StatCard("Customers", uiState.customersCount.toString(), Modifier.weight(1f))
                }
            }

            uiState.continueDraft?.let { draft ->
                item {
                    Column {
                        Text("Continue Draft", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        AppraisalCardView(card = draft, onClick = { onOpenAppraisal(draft.appraisal.id) })
                    }
                }
            }

            item {
                Text("Recent Appraisals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(uiState.recentAppraisals) { card ->
                AppraisalCardView(card = card, onClick = { onOpenAppraisal(card.appraisal.id) })
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: androidx.compose.ui.graphics.Color = LuxeColors.Navy) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = LuxeColors.OnSurfaceMuted)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.displayLarge, color = valueColor)
        }
    }
}

private fun currentDateLabel(): String {
    val now = kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    val day = now.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val month = now.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$day, $month ${now.dayOfMonth}, ${now.year}"
}