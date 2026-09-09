package com.example.luxeval.presentation.itemEditor.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.MetalColor
import com.example.luxeval.domain.MetalType
import com.example.luxeval.domain.label
import com.example.luxeval.presentation.theme.LuxeSpacing
import androidx.compose.ui.Alignment
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

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
        Text(stringResource(Res.string.section_material_analysis), style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = stringResource(Res.string.field_primary_metal),
                options = MetalType.entries, selected = state.metalType,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(metalType = it)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.purity, onValueChange = { onChange(state.copy(purity = it)) },
                label = { Text(stringResource(Res.string.field_purity)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
        }
        FormDropdown(
            label = stringResource(Res.string.field_color),
            options = MetalColor.entries, selected = state.color,
            optionLabel = { stringResource(it.label()) },
            onSelected = { onChange(state.copy(color = it)) }
        )
        OutlinedTextField(
            value = state.netWeightGrams, onValueChange = { onChange(state.copy(netWeightGrams = it)) },
            label = { Text(stringResource(Res.string.field_net_weight)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = state.isPlated, onCheckedChange = { onChange(state.copy(isPlated = it)) })
            Text(stringResource(Res.string.field_plated))
        }
        if (state.isPlated) {
            OutlinedTextField(
                value = state.platingBaseMetal, onValueChange = { onChange(state.copy(platingBaseMetal = it)) },
                label = { Text(stringResource(Res.string.field_base_metal)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
        }
    }
}