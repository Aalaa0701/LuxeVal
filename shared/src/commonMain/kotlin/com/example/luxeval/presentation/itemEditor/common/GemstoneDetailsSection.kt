package com.example.luxeval.presentation.itemEditor.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.*
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

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
            Text(stringResource(Res.string.section_gemstone), style = MaterialTheme.typography.titleSmall)
            Switch(checked = state.enabled, onCheckedChange = { onChange(state.copy(enabled = it)) })
        }
        if (!state.enabled) return@Column

        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            FormDropdown(
                label = stringResource(Res.string.field_stone_type),
                options = StoneType.entries, selected = state.stoneType,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(stoneType = it)) }, modifier = Modifier.weight(1f)
            )
            FormDropdown(
                label = stringResource(Res.string.field_origin),
                options = StoneOriginType.entries, selected = state.originType,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(originType = it)) }, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.caratWeight, onValueChange = { onChange(state.copy(caratWeight = it)) },
                label = { Text(stringResource(Res.string.field_carat_weight)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
            FormDropdown(
                label = stringResource(Res.string.field_shape), // "Shape" — see note below
                options = StoneShape.entries, selected = state.shape,
                optionLabel = { stringResource(it.label()) },
                onSelected = { onChange(state.copy(shape = it)) }, modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.colorGrade, onValueChange = { onChange(state.copy(colorGrade = it)) },
                label = { Text(stringResource(Res.string.field_color_grade)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
            OutlinedTextField(
                value = state.clarityGrade, onValueChange = { onChange(state.copy(clarityGrade = it)) },
                label = { Text(stringResource(Res.string.field_clarity_grade)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
        }
        FormDropdown(
            label = stringResource(Res.string.field_setting_type),
            options = SettingType.entries, selected = state.settingType,
            optionLabel = { stringResource(it.label()) },
            onSelected = { onChange(state.copy(settingType = it)) }
        )
        OutlinedTextField(
            value = state.measurementsMm, onValueChange = { onChange(state.copy(measurementsMm = it)) },
            label = { Text(stringResource(Res.string.field_measurements)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
            OutlinedTextField(
                value = state.certificationLab, onValueChange = { onChange(state.copy(certificationLab = it)) },
                label = { Text(stringResource(Res.string.field_cert_lab)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
            OutlinedTextField(
                value = state.certificationNumber, onValueChange = { onChange(state.copy(certificationNumber = it)) },
                label = { Text(stringResource(Res.string.field_cert_number)) },
                modifier = Modifier.weight(1f), singleLine = true
            )
        }
    }
}