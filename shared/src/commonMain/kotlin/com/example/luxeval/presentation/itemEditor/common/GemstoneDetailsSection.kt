package com.example.luxeval.presentation.itemEditor.common


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.theme.LuxeSpacing

data class GemstoneFormState(
    val enabled: Boolean = false,
    val stoneType: StoneType = StoneType.DIAMOND,
    val originType: StoneOriginType = StoneOriginType.NATURAL,
    val caratWeight: String = "",
    val shape: StoneShape = StoneShape.ROUND,
    val colorGrade: String = "",
    val clarityGrade: String = "",
    val treatment: StoneTreatment = StoneTreatment.NONE,
    val fluorescence: Fluorescence? = null,
    val settingType: SettingType = SettingType.PRONG,
    val stoneCount: String = "1",
    val certificationLab: String = "",
    val certificationNumber: String = "",
    val measurementsMm: String = ""
)

@Composable
fun GemstoneDetailsSection(
    state: GemstoneFormState,
    onChange: (GemstoneFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Gemstone", style = MaterialTheme.typography.titleSmall)
            Switch(checked = state.enabled, onCheckedChange = { onChange(state.copy(enabled = it)) })
        }
        if (!state.enabled) return@Column

        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = "Stone Type", options = StoneType.entries, selected = state.stoneType,
                optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(stoneType = it)) }, modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = "Origin", options = StoneOriginType.entries, selected = state.originType,
                optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(originType = it)) }, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.caratWeight, onValueChange = { onChange(state.copy(caratWeight = it)) },
                label = { Text("Carat Weight") }, modifier = Modifier.weight(1f), singleLine = true
            )
            FormDropdown(
                label = "Shape", options = StoneShape.entries, selected = state.shape,
                optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                onSelected = { onChange(state.copy(shape = it)) }, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.colorGrade, onValueChange = { onChange(state.copy(colorGrade = it)) },
                label = { Text("Color Grade") }, modifier = Modifier.weight(1f), singleLine = true
            )
            OutlinedTextField(
                value = state.clarityGrade, onValueChange = { onChange(state.copy(clarityGrade = it)) },
                label = { Text("Clarity Grade") }, modifier = Modifier.weight(1f), singleLine = true
            )
        }
        FormDropdown(
            label = "Setting Type", options = SettingType.entries, selected = state.settingType,
            optionLabel = { it.name.lowercase().replaceFirstChar(Char::uppercase) },
            onSelected = { onChange(state.copy(settingType = it)) }
        )
        OutlinedTextField(
            value = state.measurementsMm, onValueChange = { onChange(state.copy(measurementsMm = it)) },
            label = { Text("Measurements (mm)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.certificationLab, onValueChange = { onChange(state.copy(certificationLab = it)) },
                label = { Text("Cert. Lab") }, modifier = Modifier.weight(1f), singleLine = true
            )
            OutlinedTextField(
                value = state.certificationNumber, onValueChange = { onChange(state.copy(certificationNumber = it)) },
                label = { Text("Cert. Number") }, modifier = Modifier.weight(1f), singleLine = true
            )
        }
    }
}