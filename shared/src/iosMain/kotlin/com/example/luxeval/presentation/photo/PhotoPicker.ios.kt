package com.example.luxeval.presentation.photo

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile
import kotlin.uuid.Uuid
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.uikit.LocalUIViewController
import dev.whyoleg.cryptography.providers.base.toNSData
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.compose.koinInject
import platform.Foundation.dataWithContentsOfFile
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.coroutines.resume
import platform.UniformTypeIdentifiers.UTTypeImage

@OptIn(ExperimentalForeignApi::class)
private class PhotoPickerDelegate(
    private val onFinish: (List<PHPickerResult>) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        @Suppress("UNCHECKED_CAST")
        onFinish(didFinishPicking as List<PHPickerResult>)
    }
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun loadJpegData(
    result: PHPickerResult
): NSData? =
    suspendCancellableCoroutine { cont ->
        val provider = result.itemProvider

        if (!provider.hasItemConformingToTypeIdentifier(UTTypeImage.identifier)) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        provider.loadDataRepresentationForTypeIdentifier(
            UTTypeImage.identifier
        ) { data, error ->
            if (error != null || data == null) {
                cont.resume(null)
            } else {
                cont.resume(data)
            }
        }
    }

@Composable
actual fun rememberPhotoPickerLauncher(onPhotosPicked: (List<String>) -> Unit): PhotoPickerLauncher {
    val photoStorage: PhotoStorage = koinInject()
    val rootViewController = LocalUIViewController.current
    val scope = rememberCoroutineScope()

    // Held so the delegate isn't deallocated before the picker calls back.
    var strongDelegateRef: PhotoPickerDelegate? = null

    return remember {
        object : PhotoPickerLauncher {
            override fun launch() {
                val config = PHPickerConfiguration().apply {
                    selectionLimit = 5
                    filter = PHPickerFilter.imagesFilter
                }
                val picker = PHPickerViewController(configuration = config)
                val delegate = PhotoPickerDelegate { results ->
                    scope.launch {
                        val paths = results.mapNotNull { result ->
                            loadJpegData(result)?.let { data ->
                                photoStorage.saveImage(data.toByteArrayKt())
                            }
                        }
                        onPhotosPicked(paths)
                    }
                    rootViewController.dismissViewControllerAnimated(true, null)
                    strongDelegateRef = null
                }
                strongDelegateRef = delegate
                picker.delegate = delegate
                rootViewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class PhotoStorage {
    actual suspend fun saveImage(bytes: ByteArray, extension: String): String {
//        val image = UIImage(data = bytes.toNSData())
//        val resized = image.resized(maxDimension = 1600.0)
//        val jpegData = UIImageJPEGRepresentation(resized, 0.8) ?: bytes.toNSData()
        val docsDir = requireNotNull(
            NSFileManager.defaultManager
                .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
                .firstOrNull() as? platform.Foundation.NSURL
        ).path!!
        val photosDir = "$docsDir/photos"
        NSFileManager.defaultManager.createDirectoryAtPath(
            photosDir, true, null, null
        )
        @OptIn(kotlin.uuid.ExperimentalUuidApi::class)
        val path = "$photosDir/${Uuid.random()}.$extension"
        val data = bytes.usePinned {
            NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong())
        }
        data.writeToFile(path, true)
        return path
    }

    actual suspend fun deleteImage(path: String) {
        runCatching {
            NSFileManager.defaultManager.removeItemAtPath(path, null)
        }
    }
    // PhotoPicker.ios.kt
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun readImage(path: String): ByteArray {
        val data = NSData.dataWithContentsOfFile(path)
            ?: error("No file at $path")
        return data.toByteArrayKt()
    }

}