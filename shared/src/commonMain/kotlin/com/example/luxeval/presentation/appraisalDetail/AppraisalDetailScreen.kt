package com.example.luxeval.presentation.appraisalDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
@Composable
fun AppraisalDetailScreen(
    viewModel: AppraisalDetailViewModel,
    onOpenAppraisal: (String) -> Unit
) {
    Box(contentAlignment = Alignment.Center){
        Text("appraisal detail screen")
    }
}