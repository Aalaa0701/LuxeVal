package com.example.luxeval.presentation.itemEditor.ring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.RingBandStyle
import com.example.luxeval.presentation.itemEditor.common.*
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingEditorScreen(
    viewModel: RingEditorViewModel,
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
                title = { Text("Ring Editor") },
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
                    OutlinedTextField(
                        value = uiState.itemName,
                        onValueChange = { v -> viewModel.update { it.copy(itemName = v) } },
                        label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.hallmarks,
                        onValueChange = { v -> viewModel.update { it.copy(hallmarks = v) } },
                        label = { Text("Hallmarks / Stamps") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.ringSize,
                            onValueChange = { v -> viewModel.update { it.copy(ringSize = v) } },
                            label = { Text("Ring Size") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.bandWidthMm,
                            onValueChange = { v -> viewModel.update { it.copy(bandWidthMm = v) } },
                            label = { Text("Band Width (mm)") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    FormDropdown(
                        label = "Band Style", options = RingBandStyle.entries, selected = uiState.bandStyle,
                        optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                        onSelected = { v -> viewModel.update { it.copy(bandStyle = v) } }
                    )
                }
            }
            item {
                MetalDetailsSection(
                    state = uiState.metal,
                    onChange = { m -> viewModel.update { it.copy(metal = m) } }
                )
            }
            item {
                GemstoneDetailsSection(
                    state = uiState.gemstone,
                    onChange = { g -> viewModel.update { it.copy(gemstone = g) } }
                )
            }
            item {
                ValuationSection(
                    estimatedValue = uiState.estimatedValue,
                    onFetchMarketPrice = {viewModel.fetchMarketPrice()},
                    onValueChanged = { v -> viewModel.update { it.copy(estimatedValue = v) } },
                    notes = uiState.notes,
                    onNotesChanged = { v -> viewModel.update { it.copy(notes = v) } },
                    valueSource = uiState.valueSource,

                )
                uiState.saveError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}