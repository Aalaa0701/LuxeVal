package com.example.luxeval.presentation.itemEditor.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.theme.LuxeSpacing
import androidx.compose.ui.Alignment
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

data class PearlFormState(
    val pearlType: PearlType = PearlType.AKOYA,
    val sizeMm: String = "",
    val shape: PearlShape = PearlShape.ROUND,
    val luster: PearlLuster = PearlLuster.HIGH,
    val surfaceQuality: PearlSurfaceQuality = PearlSurfaceQuality.CLEAN,
    val overtoneColor: String = "",
    val pearlCount: String = "1",
    val isMatchedStrand: Boolean = true
)

@Composable
fun PearlDetailsSection(
    state: PearlFormState,
    onChange: (PearlFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
        Text(stringResource(Res.string.editor_title_pearl), style = MaterialTheme.typography.titleSmall)
        FormDropdown(
            label = stringResource(Res.string.field_origin_type),
            options = PearlType.entries, selected = state.pearlType,
            optionLabel = { stringResource(it.label()) },
            onSelected = { onChange(state.copy(pearlType = it)) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = stringResource(Res.string.field_shape),
                options = PearlShape.entries, selected = state.shape,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(shape = it)) }, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.sizeMm, onValueChange = { onChange(state.copy(sizeMm = it)) },
                label = { Text(stringResource(Res.string.field_size_mm)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
        }
        OutlinedTextField(
            value = state.overtoneColor, onValueChange = { onChange(state.copy(overtoneColor = it)) },
            label = { Text(stringResource(Res.string.field_overtone_color)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Text(stringResource(Res.string.label_gia_grading), style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = stringResource(Res.string.field_luster),
                options = PearlLuster.entries, selected = state.luster,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(luster = it)) }, modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = stringResource(Res.string.field_surface_quality),
                options = PearlSurfaceQuality.entries, selected = state.surfaceQuality,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(surfaceQuality = it)) }, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.pearlCount, onValueChange = { onChange(state.copy(pearlCount = it)) },
                label = { Text(stringResource(Res.string.field_pearl_count)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.isMatchedStrand,
                    onCheckedChange = { onChange(state.copy(isMatchedStrand = it)) }
                )
                Text(stringResource(Res.string.field_matched_strand))
            }
        }
    }
}