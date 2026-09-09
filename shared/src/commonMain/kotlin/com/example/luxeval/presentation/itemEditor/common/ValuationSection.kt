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
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ValuationSection(
    estimatedValue: String,
    onValueChanged: (String) -> Unit,
    onFetchMarketPrice: (() -> Unit)? = null,
    isFetchingPrice: Boolean = false,
    valueSource: ValueSource,
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
        Text(stringResource(Res.string.section_appraisal_value), style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = estimatedValue,
                onValueChange = onValueChanged,
                label = { Text(stringResource(Res.string.field_estimated_value)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            if (onFetchMarketPrice != null) {
                Spacer(Modifier.width(LuxeSpacing.sm))
                if (isFetchingPrice) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(onClick = onFetchMarketPrice) {
                        Icon(
                            painterResource(Res.drawable.ic_money),
                            contentDescription = stringResource(Res.string.cd_fetch_market_price)
                        )
                    }
                }
            }
        }
        if (valueSource == ValueSource.API_FETCHED) {
            Text(
                stringResource(Res.string.hint_market_price_source),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OutlinedTextField(
            value = notes, onValueChange = onNotesChanged,
            label = { Text(stringResource(Res.string.field_appraiser_notes_internal)) },
            modifier = Modifier.fillMaxWidth(), minLines = 3
        )
    }
}