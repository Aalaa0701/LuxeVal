package com.example.luxeval.presentation.util


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun LuxeTopBar(
    title: String = stringResource(Res.string.app_name),
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = { Text(title) },
        actions = {
            IconButton(onClick = onNotificationsClick) {
//                Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
            }
            IconButton(onClick = onProfileClick) {
                // Placeholder avatar circle — swap for AsyncImage once profile photos are wired.
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.size(32.dp).padding(end = 8.dp).clip(CircleShape)
                )
            }
        }
    )
}