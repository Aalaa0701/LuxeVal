package com.example.luxeval.presentation.itemEditor.common


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.domain.ValueSource
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_money
import org.jetbrains.compose.resources.painterResource

@Composable
fun ValuationSection(
    estimatedValue: String,
    onValueChanged: (String) -> Unit,
    onFetchMarketPrice: (() -> Unit)? = null,   // null when category has no metal (e.g. LooseGemstone could still support it via carat pricing later)
    isFetchingPrice: Boolean = false,
    valueSource: ValueSource,
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
        Text("Appraisal Value", style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = estimatedValue,
                onValueChange = onValueChanged,   // any manual edit should flip ValueSource -> MANUAL in the ViewModel
                label = { Text("Estimated Value (USD)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            if (onFetchMarketPrice != null) {
                Spacer(Modifier.width(LuxeSpacing.sm))
                if (isFetchingPrice) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(onClick = onFetchMarketPrice) {
                        Icon(painterResource(Res.drawable.ic_money), contentDescription = "Fetch market price")
                    }
                }
            }
        }
        if (valueSource == ValueSource.API_FETCHED) {
            Text("Sourced from live market price — edit to override", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedTextField(
            value = notes, onValueChange = onNotesChanged,
            label = { Text("Appraiser Notes (Internal)") }, modifier = Modifier.fillMaxWidth(), minLines = 3
        )
    }
}