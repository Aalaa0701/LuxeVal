package com.example.luxeval.presentation.itemEditor.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.theme.LuxeSpacing
import androidx.compose.ui.Alignment


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
        Text("Pearl Characteristics", style = MaterialTheme.typography.titleSmall)
        FormDropdown(
            label = "Origin Type", options = PearlType.entries, selected = state.pearlType,
            optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
            onSelected = { onChange(state.copy(pearlType = it)) }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = "Shape", options = PearlShape.entries, selected = state.shape,
                optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(shape = it)) }, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.sizeMm, onValueChange = { onChange(state.copy(sizeMm = it)) },
                label = { Text("Size (mm)") }, modifier = Modifier.weight(1f), singleLine = true
            )
        }
        OutlinedTextField(
            value = state.overtoneColor, onValueChange = { onChange(state.copy(overtoneColor = it)) },
            label = { Text("Body Color / Overtone") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Text("Quality Grading (GIA System)", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = "Luster", options = PearlLuster.entries, selected = state.luster,
                optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(luster = it)) }, modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = "Surface", options = PearlSurfaceQuality.entries, selected = state.surfaceQuality,
                optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(surfaceQuality = it)) }, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.pearlCount, onValueChange = { onChange(state.copy(pearlCount = it)) },
                label = { Text("Pearl Count") }, modifier = Modifier.weight(1f), singleLine = true
            )
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.isMatchedStrand,
                    onCheckedChange = { onChange(state.copy(isMatchedStrand = it)) }
                )
                Text("Matched Strand")
            }
        }
    }
}