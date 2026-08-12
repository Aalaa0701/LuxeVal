package com.example.luxeval.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.luxeval.data.ListStringConverters
import com.example.luxeval.data.database.daos.AppraisalDao
import com.example.luxeval.data.database.daos.ClientDao
import com.example.luxeval.data.database.daos.ItemDao
import com.example.luxeval.data.database.entities.*

@Database(
    entities = [
        ClientEntity::class,
        AppraisalEntity::class,
        ItemEntity::class,
        RingDetailsEntity::class,
        NecklaceDetailsEntity::class,
        PendantDetailsEntity::class,
        BraceletDetailsEntity::class,
        EarringsDetailsEntity::class,
        BroochDetailsEntity::class,
        PearlDetailsEntity::class,
        WatchDetailsEntity::class,
        LooseGemstoneDetailsEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ListStringConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun appraisalDao(): AppraisalDao
    abstract fun itemDao(): ItemDao
}

// KSP generates the platform actuals for this — you don't write a body.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}