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
}
