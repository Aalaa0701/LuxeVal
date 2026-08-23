package com.example.luxeval.presentation.photo

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArrayKt(): ByteArray {
    val size = length.toInt()
    val bytes = ByteArray(size)
    if (size > 0) {
        bytes.usePinned { memcpy(it.addressOf(0), this.bytes, size.toULong()) }
    }
    return bytes
}