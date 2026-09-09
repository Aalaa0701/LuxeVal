// presentation/itemEditor/earrings/EarringsEditorScreen.kt
package com.example.luxeval.presentation.itemEditor.earrings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.luxeval.domain.EarringBacking
import com.example.luxeval.domain.EarringStyle
import com.example.luxeval.domain.label
import com.example.luxeval.presentation.itemEditor.common.*
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.ErrorDialog
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_save_item
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.editor_title_earrings
import luxeval.shared.generated.resources.field_backing
import luxeval.shared.generated.resources.field_confirmed_pair
import luxeval.shared.generated.resources.field_hoop_diameter
import luxeval.shared.generated.resources.field_item_name
import luxeval.shared.generated.resources.field_style
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarringsEditorScreen(
    viewModel: EarringsEditorViewModel,
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
                title = { Text(stringResource(Res.string.editor_title_earrings)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back), modifier = Modifier.scale(
                    scaleX = if (LocalLayoutDirection.current == LayoutDirection.Rtl) -1f else 1f,
                    scaleY = 1f
                )) } },
                actions = {
                    TextButton(onClick = { viewModel.save(onSaved) }, enabled = uiState.canSave && !uiState.isSaving) {
                        Text(stringResource(Res.string.action_save_item))
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
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
                        value = uiState.itemName, onValueChange = { v -> viewModel.update { it.copy(itemName = v) } },
                        label = { Text(stringResource(Res.string.field_item_name)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(LuxeSpacing.sm)) {
                        FormDropdown(
                            label = stringResource(Res.string.field_style), options = EarringStyle.entries, selected = uiState.style,
                            optionLabel = { stringResource(it.label()) },
                            onSelected = { v -> viewModel.update { it.copy(style = v) } }, modifier = Modifier.weight(1f)
                        )
                        FormDropdown(
                            label = stringResource(Res.string.field_backing), options = EarringBacking.entries, selected = uiState.backing,
                            optionLabel = { stringResource(it.label()) },
                            onSelected = { v -> viewModel.update { it.copy(backing = v) } }, modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = uiState.hoopDiameterMm, onValueChange = { v -> viewModel.update { it.copy(hoopDiameterMm = v) } },
                        label = { Text(stringResource(Res.string.field_hoop_diameter)) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.isConfirmedPair, onCheckedChange = { v -> viewModel.update { it.copy(isConfirmedPair = v) } })
                        Text(stringResource(Res.string.field_confirmed_pair))
                    }
                }
            }
            item { MetalDetailsSection(state = uiState.metal, onChange = { m -> viewModel.update { it.copy(metal = m) } }) }
            item { GemstoneDetailsSection(state = uiState.gemstone, onChange = { g -> viewModel.update { it.copy(gemstone = g) } }) }
            item {
                ValuationSection(
                    estimatedValue = uiState.estimatedValue,
                    onValueChanged = { v -> viewModel.update { it.copy(estimatedValue = v) } },
                    notes = uiState.notes,
                    onNotesChanged = { v -> viewModel.update { it.copy(notes = v) } },
                    valueSource = uiState.valueSource,

                )
                uiState.saveError?.let {
                    ErrorDialog(error = uiState.saveError, onDismiss = viewModel::dismissSaveError, onRetry = { viewModel.save(onSaved) }) }
            }
        }
    }
}