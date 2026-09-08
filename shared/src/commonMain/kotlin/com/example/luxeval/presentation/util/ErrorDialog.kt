package com.example.luxeval.presentation.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

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
                TextButton(onClick = { onDismiss(); onRetry() }) { Text("Try Again") }
            } else {
                TextButton(onClick = onDismiss) { Text("OK") }
            }
        },
        dismissButton = {
            if (error.retryable && onRetry != null) {
                TextButton(onClick = onDismiss) { Text("Dismiss") }
            }
        }
    )
}