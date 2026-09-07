package com.example.luxeval.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.example.luxeval.data.ListStringConverters
import com.example.luxeval.data.database.daos.AppDao
import com.example.luxeval.data.database.daos.AppraisalDao
import com.example.luxeval.data.database.daos.ClientDao
import com.example.luxeval.data.database.daos.ItemDao
import com.example.luxeval.data.database.entities.AppraisalEntity
import com.example.luxeval.data.database.entities.BraceletDetailsEntity
import com.example.luxeval.data.database.entities.BroochDetailsEntity
import com.example.luxeval.data.database.entities.ClientEntity
import com.example.luxeval.data.database.entities.EarringsDetailsEntity
import com.example.luxeval.data.database.entities.ItemEntity
import com.example.luxeval.data.database.entities.LooseGemstoneDetailsEntity
import com.example.luxeval.data.database.entities.NecklaceDetailsEntity
import com.example.luxeval.data.database.entities.PearlDetailsEntity
import com.example.luxeval.data.database.entities.PendantDetailsEntity
import com.example.luxeval.data.database.entities.RingDetailsEntity
import com.example.luxeval.data.database.entities.WatchDetailsEntity

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
    abstract fun itemDao(): ItemDao
    abstract fun clientDao(): ClientDao
    abstract fun appraisalDao(): AppraisalDao
    abstract fun appDao(): AppDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}