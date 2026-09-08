package com.example.luxeval.presentation.settings


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.presentation.theme.LuxeSpacing
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.action_cancel
import luxeval.shared.generated.resources.action_delete
import luxeval.shared.generated.resources.business_profile_title
import luxeval.shared.generated.resources.delete_account_confirm_body
import luxeval.shared.generated.resources.delete_forever
import luxeval.shared.generated.resources.settings_delete_account
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteAccountDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }
    val canConfirm = confirmText.trim().equals(stringResource(Res.string.action_delete), ignoreCase = false) && !isDeleting

    AlertDialog(
        onDismissRequest = { if (!isDeleting) onDismiss() },
        title = { Text(stringResource(Res.string.settings_delete_account)) },
        text = {
            Column {
                Text(
                    stringResource(Res.string.delete_account_confirm_body),
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
                    Text(stringResource(Res.string.delete_forever), color = MaterialTheme.colorScheme.error)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isDeleting) { Text(stringResource(Res.string.action_cancel)) }
        }
    )
}