package com.example.luxeval.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource

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
                title = { Text("Reset Password") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painterResource(Res.drawable.ic_back), contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(LuxeSpacing.lg)) {
            Text(
                "Enter the email associated with your account and we'll send you a link to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(LuxeSpacing.lg))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(LuxeSpacing.md))
            Button(
                onClick = { viewModel.sendPasswordReset(email.trim()) },
                enabled = email.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Send Reset Link") }
        }
    }

    AuthStatusDialogHost(
        event = dialogEvent,
        onDismiss = { dialogEvent = null },
        onResendConfirmation = {},
        onSwitchToLogin = {}
    )
}