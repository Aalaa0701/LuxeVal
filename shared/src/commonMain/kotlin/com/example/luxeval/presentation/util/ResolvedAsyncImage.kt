package com.example.luxeval.presentation.util


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.luxeval.data.repository.PhotoRepository
import luxeval.shared.generated.resources.Res
import luxeval.shared.generated.resources.ic_image
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun PhotoImage(
    photoUri: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val photoRepository: PhotoRepository = koinInject()
    var localPath by remember(photoUri) { mutableStateOf<String?>(null) }
    var failed by remember(photoUri) { mutableStateOf(false) }

    LaunchedEffect(photoUri) {
        failed = false
        localPath = null
        runCatching { photoRepository.resolveLocalPath(photoUri) }
            .onSuccess { localPath = it }
            .onFailure { failed = true }
    }

    when {
        localPath != null -> AsyncImage(
            model = localPhotoModel(localPath!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
        failed -> Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_image), contentDescription = "Photo unavailable offline", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        else -> Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh))
    }
}