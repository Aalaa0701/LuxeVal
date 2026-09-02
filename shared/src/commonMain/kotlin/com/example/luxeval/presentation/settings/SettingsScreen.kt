package com.example.luxeval.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.data.MeasurementUnit
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.LuxeTopBar
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_arrow_forward
import luxeval.shared.generated.resources.ic_business
import luxeval.shared.generated.resources.ic_image
import luxeval.shared.generated.resources.ic_info
import luxeval.shared.generated.resources.ic_logout
import luxeval.shared.generated.resources.ic_measurements
import luxeval.shared.generated.resources.ic_money
import luxeval.shared.generated.resources.ic_signature
import luxeval.shared.generated.resources.ic_theme
import org.jetbrains.compose.resources.painterResource

// (label, code) pairs — no dedicated globe/language icon exists in your drawable
// set yet, so the Language row reuses ic_info below. Swap in a real icon later.
private val supportedLanguages = listOf(
    "en" to "English",
    "ar" to "العربية (Arabic)",
    "es" to "Español (Spanish)",
    "fr" to "Français (French)"
)

private fun String.toLanguageLabel(): String =
    supportedLanguages.firstOrNull { it.first == this }?.second ?: this

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToBusinessProfile: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showUnitPicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showLogOutConfirm by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val logoPickerLauncher = rememberPhotoPickerLauncher { paths ->
        paths.firstOrNull()?.let { viewModel.onLogoPathChanged(it) }
    }
    val signaturePickerLauncher = rememberPhotoPickerLauncher { paths ->
        paths.firstOrNull()?.let { viewModel.onSignaturePathChanged(it) }
    }

    Scaffold(topBar = { LuxeTopBar() }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.lg)
        ) {
            item {
                Column {
                    Text("Settings", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "Manage your account preferences and business details.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                SettingsSection(title = "Business Information") {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_business),
                        title = "Business Profile",
                        subtitle = uiState.businessName.ifBlank { "Name, Address, Contact" },
                        onClick = onNavigateToBusinessProfile
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_image),
                        title = "Brand Logo",
                        subtitle = if (uiState.logoPath != null) "Logo set" else "Update report branding",
                        onClick = { logoPickerLauncher.launch() }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_signature),
                        title = "Digital Signature",
                        subtitle = if (uiState.signaturePath != null) "Signature set" else "Manage appraisal signatures",
                        onClick = { signaturePickerLauncher.launch() }
                    )
                }
            }

            item {
                SettingsSection(title = "Preferences") {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_money),
                        title = "Currency",
                        subtitle = uiState.currencyCode,
                        onClick = { showCurrencyPicker = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_measurements),
                        title = "Measurement Units",
                        subtitle = uiState.measurementUnit.label(),
                        onClick = { showUnitPicker = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_info),
                        title = "Language",
                        subtitle = uiState.languageCode.toLanguageLabel(),
                        onClick = { showLanguagePicker = true }
                    )
                    SettingsToggleRow(
                        icon = painterResource(Res.drawable.ic_theme),
                        title = "Dark Mode",
                        subtitle = "System default",
                        checked = uiState.isDarkModeEnabled,
                        onCheckedChange = viewModel::onDarkModeChanged
                    )
                }
            }

            item {
                SettingsSection(title = null) {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_info),
                        title = "About LuxeValuation",
                        subtitle = "Version 1.0.0",
                        onClick = { showAboutDialog = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_logout),
                        title = "Log Out",
                        subtitle = null,
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showLogOutConfirm = true }
                    )
                }
            }
        }
    }

    if (showCurrencyPicker) {
        CurrencyPickerSheet(
            current = uiState.currencyCode,
            onSelected = { viewModel.onCurrencyChanged(it); showCurrencyPicker = false },
            onDismiss = { showCurrencyPicker = false }
        )
    }
    if (showUnitPicker) {
        MeasurementUnitPickerSheet(
            current = uiState.measurementUnit,
            onSelected = { viewModel.onMeasurementUnitChanged(it); showUnitPicker = false },
            onDismiss = { showUnitPicker = false }
        )
    }
    if (showLanguagePicker) {
        LanguagePickerSheet(
            current = uiState.languageCode,
            onSelected = { viewModel.onLanguageChanged(it); showLanguagePicker = false },
            onDismiss = { showLanguagePicker = false }
        )
    }
    if (showLogOutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogOutConfirm = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = { showLogOutConfirm = false; onLogOut() }) { Text("Log Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogOutConfirm = false }) { Text("Cancel") }
            }
        )
    }
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About LuxeValuation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)) {
                    Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "LuxeValuation helps certified appraisers create professional " +
                                "jewelry appraisal reports with business letterhead, item-level " +
                                "valuation, and signed PDF export.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("OK") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPickerSheet(current: String, onSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val currencies = listOf("USD", "EUR", "GBP", "CAD", "AUD")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(LuxeSpacing.md)) {
            Text("Select Currency", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(LuxeSpacing.sm))
            currencies.forEach { code ->
                ListItem(
                    headlineContent = { Text(code) },
                    trailingContent = { RadioButton(selected = code == current, onClick = { onSelected(code) }) },
                    modifier = Modifier.clickable { onSelected(code) }
                )
            }
            Spacer(Modifier.height(LuxeSpacing.md))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeasurementUnitPickerSheet(
    current: MeasurementUnit,
    onSelected: (MeasurementUnit) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(LuxeSpacing.md)) {
            Text("Measurement Units", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(LuxeSpacing.sm))
            MeasurementUnit.entries.forEach { unit ->
                ListItem(
                    headlineContent = { Text(unit.label()) },
                    trailingContent = { RadioButton(selected = unit == current, onClick = { onSelected(unit) }) },
                    modifier = Modifier.clickable { onSelected(unit) }
                )
            }
            Spacer(Modifier.height(LuxeSpacing.md))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerSheet(
    current: String,
    onSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(LuxeSpacing.md)) {
            Text("Select Language", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(LuxeSpacing.sm))
            supportedLanguages.forEach { (code, label) ->
                ListItem(
                    headlineContent = { Text(label) },
                    trailingContent = { RadioButton(selected = code == current, onClick = { onSelected(code) }) },
                    modifier = Modifier.clickable { onSelected(code) }
                )
            }
            Spacer(Modifier.height(LuxeSpacing.md))
        }
    }
}

@Composable
private fun SettingsSection(title: String?, content: @Composable ColumnScope.() -> Unit) {
    Column {
        if (title != null) {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = LuxeSpacing.sm)
            )
        }
        Card(shape = LuxeShapes.lg) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: Painter,
    title: String,
    subtitle: String?,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        headlineContent = { Text(title, color = titleColor, fontWeight = FontWeight.Medium) },
        supportingContent = subtitle?.let { { Text(it) } },
        trailingContent = {
            Icon(painterResource(Res.drawable.ic_arrow_forward), contentDescription = null)
        }
    )
}

@Composable
private fun SettingsToggleRow(
    icon: Painter,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

private fun MeasurementUnit.label() = when (this) {
    MeasurementUnit.METRIC -> "Millimeters (mm), Carats (ct)"
    MeasurementUnit.IMPERIAL -> "Inches, Points"
}