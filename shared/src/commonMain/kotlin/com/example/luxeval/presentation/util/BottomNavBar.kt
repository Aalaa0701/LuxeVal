package com.example.luxeval.presentation.util


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.luxeval.navigation.AppraisalsList
import com.example.luxeval.navigation.Dashboard
import com.example.luxeval.navigation.Search
import com.example.luxeval.navigation.Settings

enum class BottomNavDestination(val label: String) {
    HOME("Home"), APPRAISALS("Appraisals"), SEARCH("Search"), SETTINGS("Settings")
}

@Composable
fun LuxeBottomNavBar(current: BottomNavDestination, onNavigate: (BottomNavDestination) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = current == BottomNavDestination.HOME,
            onClick = { onNavigate(BottomNavDestination.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text(BottomNavDestination.HOME.label) }
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.APPRAISALS,
            onClick = { onNavigate(BottomNavDestination.APPRAISALS) },
            icon = { Icon(Icons.Filled.Description, contentDescription = null) },
            label = { Text(BottomNavDestination.APPRAISALS.label) }
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SEARCH,
            onClick = { onNavigate(BottomNavDestination.SEARCH) },
            icon = { Icon(Icons.Filled.Search, contentDescription = null) },
            label = { Text(BottomNavDestination.SEARCH.label) }
        )
        NavigationBarItem(
            selected = current == BottomNavDestination.SETTINGS,
            onClick = { onNavigate(BottomNavDestination.SETTINGS) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text(BottomNavDestination.SETTINGS.label) }
        )
    }
}