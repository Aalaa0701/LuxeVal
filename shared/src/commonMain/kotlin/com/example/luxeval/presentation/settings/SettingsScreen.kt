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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.luxeval.config.LuxValConfig
import com.example.luxeval.data.MeasurementUnit
import com.example.luxeval.data.ThemeMode
import com.example.luxeval.presentation.photo.rememberPhotoPickerLauncher
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.ErrorDialog
import com.example.luxeval.presentation.util.LuxeTopBar
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_cancel
import luxeval.shared.generated.resources.action_ok
import luxeval.shared.generated.resources.business_profile_title
import luxeval.shared.generated.resources.ic_arrow_forward
import luxeval.shared.generated.resources.ic_business
import luxeval.shared.generated.resources.ic_delete
import luxeval.shared.generated.resources.ic_image
import luxeval.shared.generated.resources.ic_info
import luxeval.shared.generated.resources.ic_logout
import luxeval.shared.generated.resources.ic_measurements
import luxeval.shared.generated.resources.ic_money
import luxeval.shared.generated.resources.ic_signature
import luxeval.shared.generated.resources.ic_theme
import luxeval.shared.generated.resources.nav_settings
import luxeval.shared.generated.resources.settings_about
import luxeval.shared.generated.resources.settings_about_body
import luxeval.shared.generated.resources.settings_brand_logo
import luxeval.shared.generated.resources.settings_business_info
import luxeval.shared.generated.resources.settings_business_profile_subtitle
import luxeval.shared.generated.resources.settings_currency
import luxeval.shared.generated.resources.settings_delete_account
import luxeval.shared.generated.resources.settings_digital_signature
import luxeval.shared.generated.resources.settings_language
import luxeval.shared.generated.resources.settings_log_out
import luxeval.shared.generated.resources.settings_log_out_confirm_body
import luxeval.shared.generated.resources.settings_logo_subtitle
import luxeval.shared.generated.resources.settings_measurement_units
import luxeval.shared.generated.resources.settings_preferences
import luxeval.shared.generated.resources.settings_privacy_policy
import luxeval.shared.generated.resources.settings_select_currency
import luxeval.shared.generated.resources.settings_select_language
import luxeval.shared.generated.resources.settings_signature_subtitle
import luxeval.shared.generated.resources.settings_subtitle
import luxeval.shared.generated.resources.settings_theme
import luxeval.shared.generated.resources.settings_version
import luxeval.shared.generated.resources.status_logo_set
import luxeval.shared.generated.resources.status_signature_set
import luxeval.shared.generated.resources.status_uploading
import luxeval.shared.generated.resources.theme_dark
import luxeval.shared.generated.resources.theme_dark_desc
import luxeval.shared.generated.resources.theme_light
import luxeval.shared.generated.resources.theme_light_desc
import luxeval.shared.generated.resources.theme_system
import luxeval.shared.generated.resources.theme_system_desc
import luxeval.shared.generated.resources.unit_imperial_description
import luxeval.shared.generated.resources.unit_metric_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val supportedLanguages = listOf(
    "en" to "English",
    "ar" to "العربية (Arabic)",
    "es" to "Español (Spanish)",
    "fr" to "Français (French)"
)

private fun String.toLanguageLabel(): String =
    supportedLanguages.firstOrNull { it.first == this }?.second ?: this

@Composable
private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> stringResource(Res.string.theme_system)
    ThemeMode.LIGHT -> stringResource(Res.string.theme_light)
    ThemeMode.DARK -> stringResource(Res.string.theme_dark)
}

@OptIn(ExperimentalMaterial3Api::class)
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
    var showThemePicker by remember { mutableStateOf(false) }
    var showLogOutConfirm by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }
    val isDeletingAccount by viewModel.isDeletingAccount.collectAsState()

    if (showDeleteAccountConfirm) {
        DeleteAccountDialog(
            isDeleting = isDeletingAccount,
            onConfirm = { viewModel.deleteAccount(onDeleted = onLogOut) }, // reuse existing nav-away callback
            onDismiss = { showDeleteAccountConfirm = false }
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }
    if (uiState.uploadError != null) {
        ErrorDialog(
            error = uiState.uploadError,
            onDismiss = viewModel::dismissUploadError,
            onRetry = viewModel::dismissUploadError
        )
    }
//    LaunchedEffect(uiState.uploadError) {
//        uiState.uploadError?.let { message ->
//            snackbarHostState.showSnackbar("Upload failed: $message")
//            viewModel.dismissUploadError()
//        }
//    }

    val logoPickerLauncher = rememberPhotoPickerLauncher { paths ->
        paths.firstOrNull()?.let { viewModel.onLogoPathChanged(it) }
    }
    val signaturePickerLauncher = rememberPhotoPickerLauncher { paths ->
        paths.firstOrNull()?.let { viewModel.onSignaturePathChanged(it) }
    }

    Scaffold(
        topBar = { LuxeTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.lg)
        ) {
            item {
                Column {
                    Text(stringResource(Res.string.nav_settings), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                    Text(
                        stringResource(Res.string.settings_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(Res.string.settings_business_info)) {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_business),
                        title = stringResource(Res.string.business_profile_title),
                        subtitle = uiState.businessName.ifBlank { stringResource(Res.string.settings_business_profile_subtitle) },
                        onClick = onNavigateToBusinessProfile
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_image),
                        title = stringResource(Res.string.settings_brand_logo),
                        subtitle = when {
                            uiState.isUploadingLogo -> stringResource(Res.string.status_uploading)
                            uiState.logoPath != null -> stringResource(Res.string.status_logo_set)
                            else -> stringResource(Res.string.settings_logo_subtitle)
                        },
                        isLoading = uiState.isUploadingLogo,
                        onClick = { logoPickerLauncher.launch() }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_signature),
                        title = stringResource(Res.string.settings_digital_signature),
                        subtitle = when {
                            uiState.isUploadingSignature -> stringResource(Res.string.status_uploading)
                            uiState.signaturePath != null -> stringResource(Res.string.status_signature_set)
                            else -> stringResource(Res.string.settings_signature_subtitle)
                        },
                        isLoading = uiState.isUploadingSignature,
                        onClick = { signaturePickerLauncher.launch() }
                    )
                }
            }

            item {
                SettingsSection(title = stringResource(Res.string.settings_preferences)) {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_money),
                        title = stringResource(Res.string.settings_currency),
                        subtitle = uiState.currencyCode,
                        onClick = { showCurrencyPicker = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_measurements),
                        title = stringResource(Res.string.settings_measurement_units),
                        subtitle = stringResource(uiState.measurementUnit.label()),
                        onClick = { showUnitPicker = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_info),
                        title = stringResource(Res.string.settings_language),
                        subtitle = uiState.languageCode.toLanguageLabel(),
                        onClick = { showLanguagePicker = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_theme),
                        title = stringResource(Res.string.settings_theme),
                        subtitle = uiState.themeMode.label(),
                        onClick = { showThemePicker = true }
                    )
                }
            }

            item {
                val uriHandler = LocalUriHandler.current
                SettingsSection(title = null) {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_info),
                        title = stringResource(Res.string.settings_about),
                        subtitle = stringResource(Res.string.settings_version),
                        onClick = { showAboutDialog = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_info),
                        title = stringResource(Res.string.settings_privacy_policy),
                        subtitle = null,
                        onClick = { uriHandler.openUri(LuxValConfig.PRIVACY_POLICY_URL) }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_logout),
                        title = stringResource(Res.string.settings_log_out),
                        subtitle = null,
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showLogOutConfirm = true }
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_delete),
                        title = stringResource(Res.string.settings_delete_account),
                        subtitle = null,
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteAccountConfirm = true }
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
    if (showThemePicker) {
        ThemePickerSheet(
            current = uiState.themeMode,
            onSelected = { viewModel.onThemeModeChanged(it); showThemePicker = false },
            onDismiss = { showThemePicker = false }
        )
    }
    if (showLogOutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogOutConfirm = false },
            title = { Text(stringResource(Res.string.settings_log_out)) },
            text = { Text(stringResource(Res.string.settings_log_out_confirm_body)) },
            confirmButton = {
                TextButton(onClick = { showLogOutConfirm = false; onLogOut() }) { Text(stringResource(Res.string.settings_log_out)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogOutConfirm = false }) { Text(stringResource(Res.string.action_cancel)) }
            }
        )
    }
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(stringResource(Res.string.settings_about)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(LuxeSpacing.xs)) {
                    Text(stringResource(Res.string.settings_version), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        stringResource(Res.string.settings_about_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text(stringResource(Res.string.action_ok)) }
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
            Text(stringResource(Res.string.settings_select_currency), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
            Text(stringResource(Res.string.settings_measurement_units), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(LuxeSpacing.sm))
            MeasurementUnit.entries.forEach { unit ->
                ListItem(
                    headlineContent = { Text(stringResource(unit.label())) },
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
            Text(stringResource(Res.string.settings_select_language), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePickerSheet(
    current: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        Triple(
            ThemeMode.SYSTEM,
            stringResource(Res.string.theme_system),
            stringResource(Res.string.theme_system_desc)
        ),
        Triple(
            ThemeMode.LIGHT,
            stringResource(Res.string.theme_light),
            stringResource(Res.string.theme_light_desc)
        ),
        Triple(
            ThemeMode.DARK,
            stringResource(Res.string.theme_dark),
            stringResource(Res.string.theme_dark_desc)
        )
    )
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(LuxeSpacing.md)) {
            Text(stringResource(Res.string.settings_theme), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(LuxeSpacing.sm))
            options.forEach { (mode, label, description) ->
                ListItem(
                    headlineContent = { Text(label) },
                    supportingContent = { Text(description) },
                    trailingContent = { RadioButton(selected = mode == current, onClick = { onSelected(mode) }) },
                    modifier = Modifier.clickable { onSelected(mode) }
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

/** `isLoading` swaps the trailing chevron for a small spinner and disables
 *  the row's click — same visual language as ValuationSection's
 *  isFetchingPrice indicator elsewhere in the app. */
@Composable
private fun SettingsRow(
    icon: Painter,
    title: String,
    subtitle: String?,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(enabled = !isLoading, onClick = onClick),
        leadingContent = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        headlineContent = { Text(title, color = titleColor, fontWeight = FontWeight.Medium) },
        supportingContent = subtitle?.let { { Text(it) } },
        trailingContent = {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(painterResource(Res.drawable.ic_arrow_forward), contentDescription = null)
            }
        }
    )
}

private fun MeasurementUnit.label() = when (this) {
    MeasurementUnit.METRIC -> Res.string.unit_metric_description
    MeasurementUnit.IMPERIAL -> Res.string.unit_imperial_description
}