package com.example.luxeval.presentation.itemEditor.ring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.luxeval.domain.RingBandStyle
import com.example.luxeval.domain.label
import com.example.luxeval.presentation.itemEditor.common.*
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.ErrorDialog
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                title = { Text(stringResource(Res.string.editor_title_ring)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back), modifier = Modifier.scale(
                            scaleX = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f,
                            scaleY = 1f
                        ))
                    }
                },
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
                    OutlinedTextField(
                        value = uiState.itemName,
                        onValueChange = { v -> viewModel.update { it.copy(itemName = v) } },
                        label = { Text(stringResource(Res.string.field_item_name)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.hallmarks,
                        onValueChange = { v -> viewModel.update { it.copy(hallmarks = v) } },
                        label = { Text(stringResource(Res.string.field_hallmarks)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.ringSize,
                            onValueChange = { v -> viewModel.update { it.copy(ringSize = v) } },
                            label = { Text(stringResource(Res.string.field_ring_size)) },
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.bandWidthMm,
                            onValueChange = { v -> viewModel.update { it.copy(bandWidthMm = v) } },
                            label = { Text(stringResource(Res.string.field_band_width)) },
                            modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    FormDropdown(
                        label = stringResource(Res.string.field_band_style),
                        options = RingBandStyle.entries, selected = uiState.bandStyle,
                        optionLabel = { stringResource(it.label()) },
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
                    onFetchMarketPrice = { viewModel.fetchMarketPrice() },
                    onValueChanged = { v -> viewModel.update { it.copy(estimatedValue = v) } },
                    notes = uiState.notes,
                    onNotesChanged = { v -> viewModel.update { it.copy(notes = v) } },
                    valueSource = uiState.valueSource,
                )
                uiState.saveError?.let {
                    ErrorDialog(error = uiState.saveError, onDismiss = viewModel::dismissSaveError, onRetry = { viewModel.save(onSaved) })
                }
            }
        }
    }
}