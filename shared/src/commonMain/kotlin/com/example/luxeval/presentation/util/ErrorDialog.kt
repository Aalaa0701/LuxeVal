package com.example.luxeval.presentation.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_dismiss
import luxeval.shared.generated.resources.action_ok
import luxeval.shared.generated.resources.action_try_again
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorDialog(
    error: UserError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null
) {
    if (error == null) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(error.title) },
        text = { Text(error.message) },
        confirmButton = {
            if (error.retryable && onRetry != null) {
                TextButton(onClick = { onDismiss(); onRetry() }) { Text(stringResource(Res.string.action_try_again)) }
            } else {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_ok)) }
            }
        },
        dismissButton = {
            if (error.retryable && onRetry != null) {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_dismiss)) }
            }
        }
    )
}