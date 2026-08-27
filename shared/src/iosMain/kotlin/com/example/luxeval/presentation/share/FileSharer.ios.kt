package com.example.luxeval.presentation.share

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class FileSharer {
    actual fun shareFile(path: String, mimeType: String) {
        val url = NSURL.fileURLWithPath(path)
        val activityVC = UIActivityViewController(
            activityItems = listOf(url),
            applicationActivities = null
        )
        val root = UIApplication.sharedApplication.keyWindow?.rootViewController
        root?.presentViewController(activityVC, animated = true, completion = null)
    }
}