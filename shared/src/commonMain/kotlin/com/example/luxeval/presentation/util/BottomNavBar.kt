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
import luxeval.shared.generated.resources.nav_appraisals
import luxeval.shared.generated.resources.nav_home
import luxeval.shared.generated.resources.nav_search
import luxeval.shared.generated.resources.nav_settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

enum class BottomNavDestination { HOME, APPRAISALS, SEARCH, SETTINGS }

@Composable
private fun BottomNavDestination.label(): String = when (this) {
    BottomNavDestination.HOME -> stringResource(Res.string.nav_home)
    BottomNavDestination.APPRAISALS -> stringResource(Res.string.nav_appraisals)
    BottomNavDestination.SEARCH -> stringResource(Res.string.nav_search)
    BottomNavDestination.SETTINGS -> stringResource(Res.string.nav_settings)
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
            label = { Text(stringResource(Res.string.nav_home)) },
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.APPRAISALS,
            onClick = { onNavigate(BottomNavDestination.APPRAISALS) },
            icon = { Icon(painterResource(Res.drawable.ic_appraisal), contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_appraisals)) },
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SEARCH,
            onClick = { onNavigate(BottomNavDestination.SEARCH) },
            icon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_search)) },
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SETTINGS,
            onClick = { onNavigate(BottomNavDestination.SETTINGS) },
            icon = { Icon(painterResource(Res.drawable.ic_settings), contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_settings)) },
        )
    }
}