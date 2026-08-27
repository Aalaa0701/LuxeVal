package com.example.luxeval.presentation.share


expect class FileSharer {
    /** Presents the platform's native share sheet for the file at `path`. */
    fun shareFile(path: String, mimeType: String = "application/pdf")
}