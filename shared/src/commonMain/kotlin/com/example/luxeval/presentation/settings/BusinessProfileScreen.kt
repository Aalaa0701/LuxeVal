package com.example.luxeval.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.business_address_label
import luxeval.shared.generated.resources.business_name_label
import luxeval.shared.generated.resources.business_phone_label
import luxeval.shared.generated.resources.business_profile_footer
import luxeval.shared.generated.resources.business_profile_title
import luxeval.shared.generated.resources.ic_back
import luxeval.shared.generated.resources.license_number_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.business_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(LuxeSpacing.md),
            verticalArrangement = Arrangement.spacedBy(LuxeSpacing.md)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.businessName,
                    onValueChange = viewModel::onBusinessNameChanged,
                    label = { Text(stringResource(Res.string.business_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.licenseNumber,
                    onValueChange = viewModel::onLicenseNumberChanged,
                    label = { Text(stringResource(Res.string.license_number_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.businessPhone,
                    onValueChange = viewModel::onBusinessPhoneChanged,
                    label = { Text(stringResource(Res.string.business_phone_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.businessAddress,
                    onValueChange = viewModel::onBusinessAddressChanged,
                    label = { Text(stringResource(Res.string.business_address_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
            item {
                Text(
                    stringResource(Res.string.business_profile_footer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}