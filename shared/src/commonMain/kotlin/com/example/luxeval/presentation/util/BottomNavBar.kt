package com.example.luxeval.presentation.util


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.luxeval.navigation.AppraisalsList
import com.example.luxeval.navigation.Dashboard
import com.example.luxeval.navigation.Search
import com.example.luxeval.navigation.Settings
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
    NavigationBar {
        NavigationBarItem(
            selected = current == BottomNavDestination.HOME,
            onClick = { onNavigate(BottomNavDestination.HOME) },
            icon = { Icon(painterResource(Res.drawable.ic_home), contentDescription = null) },
            label = { Text(BottomNavDestination.HOME.label) }
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.APPRAISALS,
            onClick = { onNavigate(BottomNavDestination.APPRAISALS) },
            icon = { Icon(painterResource(Res.drawable.ic_appraisal), contentDescription = null) },
            label = { Text(BottomNavDestination.APPRAISALS.label) }
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SEARCH,
            onClick = { onNavigate(BottomNavDestination.SEARCH) },
            icon = { Icon(painterResource(Res.drawable.ic_search), contentDescription = null) },
            label = { Text(BottomNavDestination.SEARCH.label) }
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SETTINGS,
            onClick = { onNavigate(BottomNavDestination.SETTINGS) },
            icon = { Icon(painterResource(Res.drawable.ic_settings), contentDescription = null) },
            label = { Text(BottomNavDestination.SETTINGS.label) }
        )
    }
}