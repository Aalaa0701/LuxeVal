package com.example.luxeval.presentation.itemEditor.necklace


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.luxeval.domain.ClaspType
import com.example.luxeval.presentation.itemEditor.common.*
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource

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
                title = { Text("Necklace Editor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_back), contentDescription = "Back") }
                 },
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
                            value = uiState.lengthCm,
                            onValueChange = { v -> viewModel.update { it.copy(lengthCm = v) } },
                            label = { Text("Length (cm)") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                        OutlinedTextField(
                            value = uiState.chainStyle,
                            onValueChange = { v -> viewModel.update { it.copy(chainStyle = v) } },
                            label = { Text("Chain Style") }, modifier = Modifier.weight(1f), singleLine = true
                        )
                    }
                    FormDropdown(
                        label = "Clasp Type",
                        options = ClaspType.entries,
                        selected = uiState.claspType ?: ClaspType.LOBSTER_CLAW,
                        optionLabel = { it.name.replace("_", " ").lowercase().replaceFirstChar(Char::uppercase) },
                        onSelected = { v -> viewModel.update { it.copy(claspType = v) } }
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.hasPendantAttached,
                            onCheckedChange = { v -> viewModel.update { it.copy(hasPendantAttached = v) } }
                        )
                        Text("Pendant Attached")
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
                    onChange = { g -> viewModel.update { it.copy(gemstone = g) } }
                )
            }
            item {
                ValuationSection(
                    estimatedValue = uiState.estimatedValue,
                    onValueChanged = { v -> viewModel.update { it.copy(estimatedValue = v) } },
                    notes = uiState.notes,
                    onNotesChanged = { v -> viewModel.update { it.copy(notes = v) } }
                )
                uiState.saveError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}