package com.example.luxeval.presentation.itemEditor.necklace


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.luxeval.domain.ClaspType
import com.example.luxeval.domain.label
import com.example.luxeval.presentation.itemEditor.common.*
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.ErrorDialog
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_save_item
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.editor_title_necklace
import luxeval.shared.generated.resources.field_chain_style
import luxeval.shared.generated.resources.field_clasp_type
import luxeval.shared.generated.resources.field_hallmarks
import luxeval.shared.generated.resources.field_item_name
import luxeval.shared.generated.resources.field_length_cm
import luxeval.shared.generated.resources.field_pendant_attached
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NecklaceEditorScreen(
    viewModel: NecklaceEditorViewModel,
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
                title = { Text(stringResource(Res.string.editor_title_necklace)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back), modifier = Modifier.scale(
                            scaleX = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f,
                            scaleY = 1f
                        )) }
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
                        label = { Text(stringResource(Res.string.field_item_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.hallmarks,
                        onValueChange = { v -> viewModel.update { it.copy(hallmarks = v) } },
                        label = { Text(stringResource(Res.string.field_hallmarks)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        OutlinedTextField(
                            value = uiState.lengthCm,
                            onValueChange = { v -> viewModel.update { it.copy(lengthCm = v) } },
                            label = { Text(stringResource(Res.string.field_length_cm)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.chainStyle,
                            onValueChange = { v -> viewModel.update { it.copy(chainStyle = v) } },
                            label = { Text(stringResource(Res.string.field_chain_style)) }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    FormDropdown(
                        label = stringResource(Res.string.field_clasp_type),
                        options = ClaspType.entries,
                        selected = uiState.claspType ?: ClaspType.LOBSTER_CLAW,
                        optionLabel = { stringResource(it.label()) },
                        onSelected = { v -> viewModel.update { it.copy(claspType = v) } }
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.hasPendantAttached,
                            onCheckedChange = { v -> viewModel.update { it.copy(hasPendantAttached = v) } }
                        )
                        Text(stringResource(Res.string.field_pendant_attached))
                    }
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
                    onChange = { g -> viewModel.update { it.copy(gemstone = g) } },
                    measurementUnit = uiState.measurementUnit
                )
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
                    ErrorDialog(error = uiState.saveError, onDismiss = viewModel::dismissSaveError, onRetry = { viewModel.save(onSaved) })
                }
            }
        }
    }
}