package com.example.luxeval.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val paths = NSFileManager.defaultManager.URLsForDirectory(
        directory = NSDocumentDirectory,
        inDomains = NSUserDomainMask
    )
    return requireNotNull(paths.firstOrNull() as? platform.Foundation.NSURL).path!!
}

fun buildDatabase(): AppDatabase =
    Room.databaseBuilder<AppDatabase>(name = documentDirectory() + "/luxeval.db")
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()