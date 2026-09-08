package com.example.luxeval.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_send_reset_link
import luxeval.shared.generated.resources.auth_email_address
import luxeval.shared.generated.resources.back
import luxeval.shared.generated.resources.ic_back
import luxeval.shared.generated.resources.reset_password_body
import luxeval.shared.generated.resources.reset_password_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(viewModel: AuthViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var dialogEvent by remember { mutableStateOf<AuthUiEvent?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            dialogEvent = event
            if (event is AuthUiEvent.PasswordResetSent) onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.reset_password_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = stringResource(Res.string.back)) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(LuxeSpacing.lg)) {
            Text(
                stringResource(Res.string.reset_password_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(LuxeSpacing.lg))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(Res.string.auth_email_address)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(LuxeSpacing.md))
            Button(
                onClick = { viewModel.sendPasswordReset(email.trim()) },
                enabled = email.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(Res.string.action_send_reset_link)) }
        }
    }

    AuthStatusDialogHost(
        event = dialogEvent,
        onDismiss = { dialogEvent = null },
        onResendConfirmation = {},
        onSwitchToLogin = {}
    )
}