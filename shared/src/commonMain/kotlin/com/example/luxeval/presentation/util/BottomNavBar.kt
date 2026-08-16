package com.example.luxeval.presentation.util


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_settings
import luxeval.shared.generated.resources.ic_search
import luxeval.shared.generated.resources.ic_appraisal
import luxeval.shared.generated.resources.ic_home
import org.jetbrains.compose.resources.painterResource

enum class BottomNavDestination(val label: String) {
    HOME("Home"), APPRAISALS("Appraisals"), SEARCH("Search"), SETTINGS("Settings")
}

@Composable
fun LuxeBottomNavBar(current: BottomNavDestination, onNavigate: (BottomNavDestination) -> Unit) {
    NavigationBar(
        modifier = Modifier.shadow(
            elevation = 8.dp,
            shape = RectangleShape
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        NavigationBarItem(
            selected = current == BottomNavDestination.HOME,
            onClick = { onNavigate(BottomNavDestination.HOME) },
            icon = { Icon(painterResource(Res.drawable.ic_home), contentDescription = null) },
            label = { Text(BottomNavDestination.HOME.label) },
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.APPRAISALS,
            onClick = { onNavigate(BottomNavDestination.APPRAISALS) },
            icon = { Icon(painterResource(Res.drawable.ic_appraisal), contentDescription = null) },
            label = { Text(BottomNavDestination.APPRAISALS.label) },
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SEARCH,
            onClick = { onNavigate(BottomNavDestination.SEARCH) },
            icon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
            label = { Text(BottomNavDestination.SEARCH.label) },
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SETTINGS,
            onClick = { onNavigate(BottomNavDestination.SETTINGS) },
            icon = { Icon(painterResource(Res.drawable.ic_settings), contentDescription = null) },
            label = { Text(BottomNavDestination.SETTINGS.label) },
        )
    }
}