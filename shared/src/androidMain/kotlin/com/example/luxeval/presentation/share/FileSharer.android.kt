package com.example.luxeval.presentation.share


import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual class FileSharer(private val context: Context) {
    actual fun shareFile(path: String, mimeType: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // context here is applicationContext, not an Activity
        }
        context.startActivity(Intent.createChooser(intent, "Share Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}