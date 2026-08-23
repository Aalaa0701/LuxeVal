package com.example.luxeval.presentation.itemEditor.common


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.luxeval.presentation.theme.LuxeSpacing

@Composable
fun ValuationSection(
    estimatedValue: String,
    onValueChanged: (String) -> Unit,
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
        Text("Appraisal Value", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = estimatedValue, onValueChange = onValueChanged,
            label = { Text("Estimated Value (USD)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = notes, onValueChange = onNotesChanged,
            label = { Text("Appraiser Notes (Internal)") }, modifier = Modifier.fillMaxWidth(), minLines = 3
        )
    }
}