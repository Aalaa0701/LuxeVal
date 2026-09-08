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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val scaled = scaleDown(bitmap, maxDimension = 1600)
            val dir = File(context.filesDir, "photos").apply { mkdirs() }
            @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
            val file = File(dir, "${Uuid.random()}.jpg")   // always jpg — see #2 orphan fix
            file.outputStream().use { out ->
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            bitmap.recycle(); if (scaled !== bitmap) scaled.recycle()
            file.absolutePath
        }

    private val cacheDir get() = File(context.filesDir, "photo_cache").apply { mkdirs() }

    actual suspend fun saveCachedFile(key: String, bytes: ByteArray): String =
        withContext(Dispatchers.IO) {
            val file = File(cacheDir, "$key.jpg")
            file.writeBytes(bytes)
            file.absolutePath
        }

    actual fun cachedFilePath(key: String): String = File(cacheDir, "$key.jpg").absolutePath
    actual fun cachedFileExists(key: String): Boolean = File(cacheDir, "$key.jpg").exists()

    actual suspend fun deleteImage(path: String) {
        runCatching { File(path).delete() }
        Unit
    }
    // PhotoPicker.android.kt
    actual suspend fun readImage(path: String): ByteArray =
        withContext(Dispatchers.IO) { File(path).readBytes() }
    private fun scaleDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val ratio = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
        if (ratio >= 1f) return bitmap
        return Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
    }
}