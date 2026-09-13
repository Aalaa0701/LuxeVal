package com.example.luxeval.presentation.itemEditor.pearl


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.luxeval.presentation.itemEditor.common.*
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.ErrorDialog
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_save_item
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.editor_title_pearl
import luxeval.shared.generated.resources.field_appraiser_notes
import luxeval.shared.generated.resources.field_clasp_metal
import luxeval.shared.generated.resources.field_item_name
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PearlEditorScreen(
    viewModel: PearlEditorViewModel,
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
                title = { Text(stringResource(Res.string.editor_title_pearl)) },
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
                OutlinedTextField(
                    value = uiState.itemName,
                    onValueChange = { v -> viewModel.update { it.copy(itemName = v) } },
                    label = { Text(stringResource(Res.string.field_item_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.conditionNotes,
                    onValueChange = { v -> viewModel.update { it.copy(conditionNotes = v) } },
                    label = { Text(stringResource(Res.string.field_appraiser_notes)) }, modifier = Modifier.fillMaxWidth(), minLines = 3
                )
            }
            item {
                PearlDetailsSection(
                    state = uiState.pearl,
                    onChange = { p -> viewModel.update { it.copy(pearl = p) } },
                    measurementUnit = uiState.measurementUnit
                )
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(Res.string.field_clasp_metal), style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = uiState.hasClaspMetal,
                            onCheckedChange = { v -> viewModel.update { it.copy(hasClaspMetal = v) } }
                        )
                    }
                    if (uiState.hasClaspMetal) {
                        MetalDetailsSection(
                            state = uiState.metal,
                            onChange = { m -> viewModel.update { it.copy(metal = m) } }
                        )
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
                    ErrorDialog(error = uiState.saveError, onDismiss = viewModel::dismissSaveError, onRetry = { viewModel.save(onSaved) })}
            }
        }
    }
}