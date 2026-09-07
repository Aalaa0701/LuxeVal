package com.example.luxeval.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val dbFile = context.getDatabasePath("luxeval.db")
    return Room.databaseBuilder<AppDatabase>(context = context, name = dbFile.absolutePath)
}
fun buildDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = "luxeval.db"
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}