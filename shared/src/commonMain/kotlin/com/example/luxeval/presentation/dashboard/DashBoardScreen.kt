package com.example.luxeval.presentation.dashboard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.util.AppraisalCardView
import com.example.luxeval.presentation.util.LuxeTopBar
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_cancel
import luxeval.shared.generated.resources.action_go_to_profile
import luxeval.shared.generated.resources.dashboard_completed
import luxeval.shared.generated.resources.dashboard_continue_draft
import luxeval.shared.generated.resources.dashboard_customers
import luxeval.shared.generated.resources.dashboard_drafts
import luxeval.shared.generated.resources.dashboard_greeting
import luxeval.shared.generated.resources.dashboard_recent_appraisals
import luxeval.shared.generated.resources.dashboard_total_appraisals
import luxeval.shared.generated.resources.dialog_incomplete_profile_body
import luxeval.shared.generated.resources.dialog_incomplete_profile_title
import luxeval.shared.generated.resources.ic_add
import luxeval.shared.generated.resources.new_appraisal
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenAppraisal: (String) -> Unit,
    onNavigateToBusinessProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showIncompleteProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = { LuxeTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createDraftAndGetId(
                        onCreated = onOpenAppraisal,
                        onProfileIncomplete = { showIncompleteProfileDialog = true }
                    )
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(painterResource(Res.drawable.ic_add), contentDescription = stringResource(Res.string.new_appraisal))
            }
        },

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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column {
                    Text(stringResource(Res.string.dashboard_greeting), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Text(currentDateLabel(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(30.dp))
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(stringResource(Res.string.dashboard_total_appraisals), uiState.totalAppraisals.toString(), Modifier.weight(1f))
                    StatCard(stringResource(Res.string.dashboard_completed), uiState.completedCount.toString(), Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(stringResource(Res.string.dashboard_drafts), uiState.draftsCount.toString(), Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.secondary)
                    StatCard(stringResource(Res.string.dashboard_customers), uiState.customersCount.toString(), Modifier.weight(1f))
                }
                Spacer(Modifier.height(30.dp))

            }

            uiState.continueDraft?.let { draft ->
                item {
                    Column {
                        Text(stringResource(Res.string.dashboard_continue_draft), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        AppraisalCardView(card = draft, onClick = { onOpenAppraisal(draft.appraisal.id) })
                        Spacer(Modifier.height(30.dp))
                    }
                }
            }

            item {
                Text(stringResource(Res.string.dashboard_recent_appraisals), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(uiState.recentAppraisals) { card ->
                AppraisalCardView(card = card, onClick = { onOpenAppraisal(card.appraisal.id) })
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
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color =
        MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge,
                color = valueColor
            )
        }
    }
}

//@Composable
//private fun StatCard(label: String, value: String, modifier: Modifier = Modifier, valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface) {
//    Card(modifier = modifier.background(MaterialTheme.colorScheme.background), shape = RoundedCornerShape(16.dp)) {
//        Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow).padding(16.dp)) {
//            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
//            Spacer(Modifier.height(6.dp))
//            Text(value, style = MaterialTheme.typography.displayLarge, color = valueColor)
//        }
//    }
//}

private fun currentDateLabel(): String {
    val now = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())

    return "${now.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, " +
            "${now.month.name.lowercase().replaceFirstChar { it.uppercase() }} " +
            "${now.dayOfMonth}, ${now.year}"
}