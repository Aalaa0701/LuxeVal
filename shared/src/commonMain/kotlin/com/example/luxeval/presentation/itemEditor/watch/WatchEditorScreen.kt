// presentation/itemEditor/watch/WatchEditorScreen.kt
package com.example.luxeval.presentation.itemEditor.watch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.WatchComplication
import com.example.luxeval.domain.WatchMovementType
import com.example.luxeval.domain.WatchRunningCondition
import com.example.luxeval.presentation.itemEditor.common.FormDropdown
import com.example.luxeval.presentation.itemEditor.common.PhotoGridEditor
import com.example.luxeval.presentation.itemEditor.common.ValuationSection
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource

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
                title = { Text("Watch Editor") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = "Back"
                ) } },
                actions = {
                    TextButton(onClick = { viewModel.save(onSaved) }, enabled = uiState.canSave && !uiState.isSaving) {
                        Text("Save Item")
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
                    Text("Basic Info", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = uiState.itemName,
                        onValueChange = { v -> viewModel.update { it.copy(itemName = v) } },
                        label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.brand,
                            onValueChange = { v -> viewModel.update { it.copy(brand = v) } },
                            label = { Text("Brand") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.model,
                            onValueChange = { v -> viewModel.update { it.copy(model = v) } },
                            label = { Text("Model") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.referenceNumber,
                            onValueChange = { v -> viewModel.update { it.copy(referenceNumber = v) } },
                            label = { Text("Reference #") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.serialNumber,
                            onValueChange = { v -> viewModel.update { it.copy(serialNumber = v) } },
                            label = { Text("Serial #") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Text("Specifications", style = MaterialTheme.typography.titleSmall)
                    FormDropdown(
                        label = "Movement", options = WatchMovementType.entries, selected = uiState.movementType,
                        optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                        onSelected = { v -> viewModel.update { it.copy(movementType = v) } }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.caseMaterial,
                            onValueChange = { v -> viewModel.update { it.copy(caseMaterial = v) } },
                            label = { Text("Case Material") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.caseSizeMm,
                            onValueChange = { v -> viewModel.update { it.copy(caseSizeMm = v) } },
                            label = { Text("Case Size (mm)") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = uiState.bandMaterial,
                        onValueChange = { v -> viewModel.update { it.copy(bandMaterial = v) } },
                        label = { Text("Bracelet / Band Material") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.dialColor,
                            onValueChange = { v -> viewModel.update { it.copy(dialColor = v) } },
                            label = { Text("Dial Color") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.waterResistance,
                            onValueChange = { v -> viewModel.update { it.copy(waterResistance = v) } },
                            label = { Text("Water Resistance") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = uiState.yearOfManufacture,
                        onValueChange = { v -> viewModel.update { it.copy(yearOfManufacture = v) } },
                        label = { Text("Year of Manufacture") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Text("Complications", style = MaterialTheme.typography.titleSmall)
                    // Simple flow of filter chips — swap for FlowRow if you add the layout lib later
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)) {
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
                    Text("Condition", style = MaterialTheme.typography.titleSmall)
                    FormDropdown(
                        label = "Running Condition", options = WatchRunningCondition.entries, selected = uiState.runningCondition,
                        optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                        onSelected = { v -> viewModel.update { it.copy(runningCondition = v) } }
                    )
                    Row {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = uiState.hasBox, onCheckedChange = { v -> viewModel.update { it.copy(hasBox = v) } })
                            Text("Has Box")
                        }
                        Spacer(Modifier.width(LuxeSpacing.md))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = uiState.hasPapers, onCheckedChange = { v -> viewModel.update { it.copy(hasPapers = v) } })
                            Text("Has Papers")
                        }
                    }
                }
            }
            item {
                ValuationSection(
                    estimatedValue = uiState.estimatedValue,
                    onValueChanged = { v -> viewModel.update { it.copy(estimatedValue = v) } },
                    notes = uiState.notes,
                    onNotesChanged = { v -> viewModel.update { it.copy(notes = v) } }
                )
                uiState.saveError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}