package com.example.luxeval.presentation.util


import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.luxeval.data.repository.PhotoRepository
import org.koin.compose.koinInject

@Composable
fun PhotoImage(
    photoUri: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val photoRepository: PhotoRepository = koinInject()
    var resolvedUrl by remember(photoUri) { mutableStateOf<String?>(null) }

    LaunchedEffect(photoUri) {
        resolvedUrl = photoRepository.resolveUrl(photoUri)
    }

    resolvedUrl?.let {
        AsyncImage(
            model = it,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}