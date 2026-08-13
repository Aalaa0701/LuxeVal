package com.example.luxeval.presentation.util


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LuxeTopBar(
    title: String = "LuxeValuation",
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    TopAppBar(
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