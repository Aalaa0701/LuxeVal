package com.example.luxeval.presentation.itemEditor.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.MetalColor
import com.example.luxeval.domain.MetalType
import com.example.luxeval.presentation.theme.LuxeSpacing
import androidx.compose.ui.Alignment


data class MetalFormState(
    val metalType: MetalType = MetalType.GOLD,
    val purity: String = "",
    val color: MetalColor = MetalColor.YELLOW,
    val netWeightGrams: String = "",
    val isPlated: Boolean = false,
    val platingBaseMetal: String = ""
)

@Composable
fun MetalDetailsSection(
    state: MetalFormState,
    onChange: (MetalFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
        Text("Material Analysis", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = "Primary Metal", options = MetalType.entries, selected = state.metalType,
                optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(metalType = it)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.purity, onValueChange = { onChange(state.copy(purity = it)) },
                label = { Text("Purity") }, modifier = Modifier.weight(1f), singleLine = true
            )
        }
        FormDropdown(
            label = "Color", options = MetalColor.entries, selected = state.color,
            optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
            onSelected = { onChange(state.copy(color = it)) }
        )
        OutlinedTextField(
            value = state.netWeightGrams, onValueChange = { onChange(state.copy(netWeightGrams = it)) },
            label = { Text("Net Metal Weight (g)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = state.isPlated, onCheckedChange = { onChange(state.copy(isPlated = it)) })
            Text("Plated")
        }
        if (state.isPlated) {
            OutlinedTextField(
                value = state.platingBaseMetal, onValueChange = { onChange(state.copy(platingBaseMetal = it)) },
                label = { Text("Base Metal") }, modifier = Modifier.fillMaxWidth(), singleLine = true
            )
        }
    }
}