package com.example.luxeval.presentation.photo


import androidx.compose.runtime.Composable

interface PhotoPickerLauncher {
    fun launch()
}

@Composable
expect fun rememberPhotoPickerLauncher(onPhotosPicked: (List<String>) -> Unit): PhotoPickerLauncher

expect class PhotoStorage {
    suspend fun saveImage(bytes: ByteArray, extension: String = "jpg"): String
    suspend fun deleteImage(path: String)
    suspend fun readImage(path: String): ByteArray
    suspend fun saveCachedFile(key: String, bytes: ByteArray): String
    fun cachedFilePath(key: String): String
    fun cachedFileExists(key: String): Boolean
    // PhotoStorage — add to both actuals
    fun enforceCacheLimit(maxBytes: Long = 250L * 1024 * 1024)
}
