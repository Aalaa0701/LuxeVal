package com.example.luxeval.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.luxeval.data.MeasurementUnit
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.LuxeSpacing
import com.example.luxeval.presentation.util.LuxeTopBar
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_arrow_forward
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import luxeval.shared.generated.resources.ic_business
import luxeval.shared.generated.resources.ic_image
import luxeval.shared.generated.resources.ic_info
import luxeval.shared.generated.resources.ic_logout
import luxeval.shared.generated.resources.ic_measurements
import luxeval.shared.generated.resources.ic_money
import luxeval.shared.generated.resources.ic_signature
import luxeval.shared.generated.resources.ic_theme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToBusinessProfile: () -> Unit = {},
    onNavigateToBrandLogo: () -> Unit = {},
    onNavigateToSignature: () -> Unit = {},
    onNavigateToCurrency: () -> Unit = {},
    onNavigateToMeasurementUnits: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

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
                        subtitle = "Update report branding",
                        onClick = onNavigateToBrandLogo
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_signature),
                        title = "Digital Signature",
                        subtitle = "Manage appraisal signatures",
                        onClick = onNavigateToSignature
                    )
                }
            }

            item {
                SettingsSection(title = "Preferences") {
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_money),
                        title = "Currency",
                        subtitle = uiState.currencyCode,
                        onClick = onNavigateToCurrency
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_measurements),
                        title = "Measurement Units",
                        subtitle = uiState.measurementUnit.label(),
                        onClick = onNavigateToMeasurementUnits
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
                        onClick = {}
                    )
                    SettingsRow(
                        icon = painterResource(Res.drawable.ic_logout),
                        title = "Log Out",
                        subtitle = null,
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = onLogOut
                    )
                }
            }
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