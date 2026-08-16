package com.example.luxeval.presentation.util


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.luxeval.domain.AppraisalStatus
import com.example.luxeval.presentation.theme.LocalDarkTheme
import com.example.luxeval.presentation.theme.LuxeShapes
import com.example.luxeval.presentation.theme.StatusColors

@Composable
fun StatusPill(status: AppraisalStatus, modifier: Modifier = Modifier) {
    val darkTheme = LocalDarkTheme.current
    val (bg, fg, label) = when (status) {
        AppraisalStatus.DRAFT -> if (darkTheme) Triple(StatusColors.DraftBgDark, StatusColors.DraftFgDark, "Draft")
        else Triple(StatusColors.DraftBg, StatusColors.DraftFg, "Draft")
        AppraisalStatus.COMPLETED -> if (darkTheme) Triple(StatusColors.CompletedBgDark, StatusColors.CompletedFgDark, "Completed")
        else Triple(StatusColors.CompletedBg, StatusColors.CompletedFg, "Completed")
        AppraisalStatus.ARCHIVED -> if (darkTheme) Triple(StatusColors.ArchivedBgDark, StatusColors.ArchivedFgDark, "Archived")
        else Triple(StatusColors.ArchivedBg, StatusColors.ArchivedFg, "Archived")
    }
    Text(
        text = "• $label", color = fg, style = MaterialTheme.typography.labelSmall,
        modifier = modifier.background(bg, LuxeShapes.full).padding(horizontal = 10.dp, vertical = 4.dp)
    )
}