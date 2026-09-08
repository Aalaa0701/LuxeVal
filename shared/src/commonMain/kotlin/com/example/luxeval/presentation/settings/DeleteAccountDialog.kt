package com.example.luxeval.presentation.settings


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LuxeSpacing

@Composable
fun DeleteAccountDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }
    val canConfirm = confirmText.trim().equals("DELETE", ignoreCase = false) && !isDeleting

    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text("Delete Account") },
        text = {
            Column {
                Text(
                    "This permanently deletes your account, all appraisals, clients, " +
                            "items, and photos. This cannot be undone.\n\nType DELETE to confirm."
                )
                Spacer(Modifier.height(LuxeSpacing.sm))
                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    singleLine = true,
                    enabled = !isDeleting,
                    modifier = Modifier
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = canConfirm) {
                if (isDeleting) {
                    CircularProgressIndicator(Modifier, strokeWidth = 2.dp)
                } else {
                    Text("Delete Forever", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) { Text("Cancel") }
        }
    )
}