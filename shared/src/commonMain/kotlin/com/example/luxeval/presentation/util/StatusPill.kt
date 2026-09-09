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
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.status_draft
import luxeval.shared.generated.resources.status_completed
import luxeval.shared.generated.resources.status_archived
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusPill(status: AppraisalStatus, modifier: Modifier = Modifier) {
    val darkTheme = LocalDarkTheme.current
    val label = when (status) {
        AppraisalStatus.DRAFT -> stringResource(Res.string.status_draft)
        AppraisalStatus.COMPLETED -> stringResource(Res.string.status_completed)
        AppraisalStatus.ARCHIVED -> stringResource(Res.string.status_archived)
    }
    val (bg, fg) = when (status) {
        AppraisalStatus.DRAFT -> if (darkTheme) StatusColors.DraftBgDark to StatusColors.DraftFgDark else StatusColors.DraftBg to StatusColors.DraftFg
        AppraisalStatus.COMPLETED -> if (darkTheme) StatusColors.CompletedBgDark to StatusColors.CompletedFgDark else StatusColors.CompletedBg to StatusColors.CompletedFg
        AppraisalStatus.ARCHIVED -> if (darkTheme) StatusColors.ArchivedBgDark to StatusColors.ArchivedFgDark else StatusColors.ArchivedBg to StatusColors.ArchivedFg
    }
    Text(
        text = "• $label", color = fg, style = MaterialTheme.typography.labelSmall,
        modifier = modifier.background(bg, LuxeShapes.full).padding(horizontal = 10.dp, vertical = 4.dp)
    )
}