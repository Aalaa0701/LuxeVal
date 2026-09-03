package com.example.luxeval.presentation.auth


import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.luxeval.presentation.theme.LuxeSpacing
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement

/** Renders the right AlertDialog for whichever AuthUiEvent needs user
 *  attention. Screens just collect `viewModel.events` and set this state —
 *  kept as one composable so the login and sign-up flows share identical
 *  dialog copy and behavior. */
@Composable
fun AuthStatusDialogHost(
    event: AuthUiEvent?,
    onDismiss: () -> Unit,
    onResendConfirmation: (email: String) -> Unit,
    onSwitchToLogin: () -> Unit
) {
    when (event) {
        null -> Unit

        is AuthUiEvent.SignUpNeedsConfirmation -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Check Your Email") },
            text = {
                Text(
                    "We've sent a confirmation link to ${event.email}. " +
                            "Please verify your email before signing in — check your spam folder if it doesn't arrive within a few minutes."
                )
            },
            confirmButton = {
                TextButton(onClick = { onDismiss(); onSwitchToLogin() }) { Text("Go to Login") }
            },
            dismissButton = {
                TextButton(onClick = { onResendConfirmation(event.email) }) { Text("Resend Email") }
            }
        )

        is AuthUiEvent.SignUpAutoLoggedIn -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Welcome!") },
            text = { Text("Your account is ready. Let's get your business profile set up.") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Continue") } }
        )

        is AuthUiEvent.EmailNotConfirmed -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Email Not Verified") },
            text = { Text("Please confirm ${event.email} before signing in. Didn't get the link?") },
            confirmButton = {
                TextButton(onClick = { onResendConfirmation(event.email); onDismiss() }) { Text("Resend Email") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        )

        is AuthUiEvent.PasswordResetSent -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Reset Link Sent") },
            text = { Text("Check your email for a link to reset your password.") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
        )

        is AuthUiEvent.ResendConfirmationSent -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Email Sent") },
            text = { Text("We've sent another confirmation link — give it a minute to arrive.") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
        )

        is AuthUiEvent.Error -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Something Went Wrong") },
            text = { Text(event.message) },
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
        )
    }
}