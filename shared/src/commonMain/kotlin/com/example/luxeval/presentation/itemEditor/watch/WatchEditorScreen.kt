// presentation/itemEditor/watch/WatchEditorScreen.kt
package com.example.luxeval.presentation.itemEditor.watch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.luxeval.domain.WatchComplication
import com.example.luxeval.domain.WatchMovementType
import com.example.luxeval.domain.WatchRunningCondition
import com.example.luxeval.domain.label
import com.example.luxeval.presentation.itemEditor.common.FormDropdown
import com.example.luxeval.presentation.itemEditor.common.PhotoGridEditor
import com.example.luxeval.presentation.itemEditor.common.ValuationSection
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.ErrorDialog
import com.example.luxeval.presentation.util.lengthUnitLabel
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_save_item
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.editor_title_watch
import luxeval.shared.generated.resources.field_band_material
import luxeval.shared.generated.resources.field_brand
import luxeval.shared.generated.resources.field_case_material
import luxeval.shared.generated.resources.field_case_size
import luxeval.shared.generated.resources.field_dial_color
import luxeval.shared.generated.resources.field_has_box
import luxeval.shared.generated.resources.field_has_papers
import luxeval.shared.generated.resources.field_item_name
import luxeval.shared.generated.resources.field_model
import luxeval.shared.generated.resources.field_movement
import luxeval.shared.generated.resources.field_reference_number
import luxeval.shared.generated.resources.field_running_condition
import luxeval.shared.generated.resources.field_serial_number
import luxeval.shared.generated.resources.field_water_resistance
import luxeval.shared.generated.resources.field_year_manufacture
import luxeval.shared.generated.resources.ic_back
import luxeval.shared.generated.resources.section_basic_info
import luxeval.shared.generated.resources.section_complications
import luxeval.shared.generated.resources.section_condition
import luxeval.shared.generated.resources.section_specifications
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchEditorScreen(
    viewModel: WatchEditorViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val photoLauncher = rememberPhotoPickerLauncher { newPaths ->
        viewModel.update { it.copy(photoUris = it.photoUris + newPaths) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.editor_title_watch)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back), modifier = Modifier.scale(
                    scaleX = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f,
                    scaleY = 1f
                )
                ) } },
                actions = {
                    TextButton(onClick = { viewModel.save(onSaved) }, enabled = uiState.canSave && !uiState.isSaving) {
                        Text(stringResource(Res.string.action_save_item))
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.lg)
        ) {
            item {
                PhotoGridEditor(
                    photoUris = uiState.photoUris,
                    onAddPhoto = { photoLauncher.launch() },
                    onRemovePhoto = { uri -> viewModel.removePhoto(uri) }
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Text(stringResource(Res.string.section_basic_info), style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = uiState.itemName,
                        onValueChange = { v -> viewModel.update { it.copy(itemName = v) } },
                        label = { Text(stringResource(Res.string.field_item_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.brand,
                            onValueChange = { v -> viewModel.update { it.copy(brand = v) } },
                            label = { Text(stringResource(Res.string.field_brand)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.model,
                            onValueChange = { v -> viewModel.update { it.copy(model = v) } },
                            label = { Text(stringResource(Res.string.field_model)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.referenceNumber,
                            onValueChange = { v -> viewModel.update { it.copy(referenceNumber = v) } },
                            label = { Text(stringResource(Res.string.field_reference_number)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.serialNumber,
                            onValueChange = { v -> viewModel.update { it.copy(serialNumber = v) } },
                            label = { Text(stringResource(Res.string.field_serial_number)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Text(stringResource(Res.string.section_specifications), style = MaterialTheme.typography.titleSmall)
                    FormDropdown(
                        label = stringResource(Res.string.field_movement), options = WatchMovementType.entries, selected = uiState.movementType,
                        optionLabel = { stringResource(it.label()) },
                        onSelected = { v -> viewModel.update { it.copy(movementType = v) } }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.caseMaterial,
                            onValueChange = { v -> viewModel.update { it.copy(caseMaterial = v) } },
                            label = { Text(stringResource(Res.string.field_case_material)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.caseSizeMm,
                            onValueChange = { v -> viewModel.update { it.copy(caseSizeMm = v) } },
                            label = { Text("${stringResource(Res.string.field_case_size)} (${lengthUnitLabel(uiState.measurementUnit)})") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = uiState.bandMaterial,
                        onValueChange = { v -> viewModel.update { it.copy(bandMaterial = v) } },
                        label = { Text(stringResource(Res.string.field_band_material)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.dialColor,
                            onValueChange = { v -> viewModel.update { it.copy(dialColor = v) } },
                            label = { Text(stringResource(Res.string.field_dial_color)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.waterResistance,
                            onValueChange = { v -> viewModel.update { it.copy(waterResistance = v) } },
                            label = { Text(stringResource(Res.string.field_water_resistance)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = uiState.yearOfManufacture,
                        onValueChange = { v -> viewModel.update { it.copy(yearOfManufacture = v) } },
                        label = { Text(stringResource(Res.string.field_year_manufacture)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Text(stringResource(Res.string.section_complications), style = MaterialTheme.typography.titleSmall)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)
                    ) {
                        WatchComplication.entries.filter { it != WatchComplication.NONE }.forEach { complication ->
                            FilterChip(
                                selected = complication in uiState.complications,
                                onClick = { viewModel.toggleComplication(complication) },
                                label = { Text(complication.name.lowercase().replaceFirstChar(Char::uppercase)) }
                            )
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Text(stringResource(Res.string.section_condition), style = MaterialTheme.typography.titleSmall)
                    FormDropdown(
                        label = stringResource(Res.string.field_running_condition), options = WatchRunningCondition.entries, selected = uiState.runningCondition,
                        optionLabel = { stringResource(it.label()) },
                        onSelected = { v -> viewModel.update { it.copy(runningCondition = v) } }
                    )
                    Row {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = uiState.hasBox, onCheckedChange = { v -> viewModel.update { it.copy(hasBox = v) } })
                            Text(stringResource(Res.string.field_has_box))
                        }
                        Spacer(Modifier.width(LuxeSpacing.md))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = uiState.hasPapers, onCheckedChange = { v -> viewModel.update { it.copy(hasPapers = v) } })
                            Text(stringResource(Res.string.field_has_papers))
                        }
                    }
                }
            }
            item {
                ValuationSection(
                    estimatedValue = uiState.estimatedValue,
                    onValueChanged = { v -> viewModel.update { it.copy(estimatedValue = v) } },
                    notes = uiState.notes,
                    onNotesChanged = { v -> viewModel.update { it.copy(notes = v) } },
                    valueSource = uiState.valueSource,
                    currencyCode = uiState.currencyCode,
                )
                uiState.saveError?.let {
                    ErrorDialog(error = uiState.saveError, onDismiss = viewModel::dismissSaveError, onRetry = { viewModel.save(onSaved) }) }
            }
        }
    }
}