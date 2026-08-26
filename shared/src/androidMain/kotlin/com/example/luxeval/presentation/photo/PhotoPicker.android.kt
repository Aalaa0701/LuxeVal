package com.example.luxeval.presentation.photo

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.uuid.Uuid

@Composable
actual fun rememberPhotoPickerLauncher(onPhotosPicked: (List<String>) -> Unit): PhotoPickerLauncher {
    val context = LocalContext.current
    val photoStorage: PhotoStorage = koinInject()
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            val paths = uris.mapNotNull { uri ->
                context.contentResolver.openInputStream(uri)
                    ?.use { it.readBytes() }
                    ?.let { bytes -> photoStorage.saveImage(bytes) }
            }
            onPhotosPicked(paths)
        }
    }

    return remember {
        object : PhotoPickerLauncher {
            override fun launch() {
                launcher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }
}
actual class PhotoStorage(private val context: Context) {
    actual suspend fun saveImage(bytes: ByteArray, extension: String): String =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "photos").apply { mkdirs() }
            val file = File(dir, "${Uuid.random()}.$extension")
            file.writeBytes(bytes)
            file.absolutePath
        }

    actual suspend fun deleteImage(path: String) {
        runCatching { File(path).delete() }
        Unit
    }

}