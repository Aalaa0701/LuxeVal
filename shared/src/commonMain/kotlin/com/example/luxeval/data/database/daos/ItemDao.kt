package com.example.luxeval.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.luxeval.data.database.entities.BraceletDetailsEntity
import com.example.luxeval.data.database.entities.BroochDetailsEntity
import com.example.luxeval.data.database.entities.EarringsDetailsEntity
import com.example.luxeval.data.database.entities.ItemEntity
import com.example.luxeval.data.database.entities.LooseGemstoneDetailsEntity
import com.example.luxeval.data.database.entities.NecklaceDetailsEntity
import com.example.luxeval.data.database.entities.PearlDetailsEntity
import com.example.luxeval.data.database.entities.PendantDetailsEntity
import com.example.luxeval.data.database.entities.RingDetailsEntity
import com.example.luxeval.data.database.entities.WatchDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // ── base item rows ──────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Query("SELECT * FROM items WHERE appraisalId = :appraisalId ORDER BY createdAt ASC")
    fun observeItemsForAppraisal(appraisalId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun observeAllItems(): Flow<List<ItemEntity>>
    @Query("SELECT * FROM items WHERE appraisalId = :appraisalId ORDER BY createdAt ASC")
    suspend fun getItemsForAppraisalOnce(appraisalId: String): List<ItemEntity>

    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItem(itemId: String) // ON DELETE CASCADE on the FK removes the matching detail row too

    // ── Ring ─────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRingDetails(details: RingDetailsEntity)

    @Query("SELECT * FROM ring_details WHERE itemId = :itemId")
    suspend fun getRingDetails(itemId: String): RingDetailsEntity?

    // ── Watch ────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchDetails(details: WatchDetailsEntity)

    @Query("SELECT * FROM watch_details WHERE itemId = :itemId")
    suspend fun getWatchDetails(itemId: String): WatchDetailsEntity?

    // ── Loose Gemstone ───────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLooseGemstoneDetails(details: LooseGemstoneDetailsEntity)

    @Query("SELECT * FROM loose_gemstone_details WHERE itemId = :itemId")
    suspend fun getLooseGemstoneDetails(itemId: String): LooseGemstoneDetailsEntity?


    // ── Necklace ────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNecklaceDetails(details: NecklaceDetailsEntity)

    @Query("SELECT * FROM necklace_details WHERE itemId = :itemId")
    suspend fun getNecklaceDetails(itemId: String): NecklaceDetailsEntity?

    // ── Pendant ─────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendantDetails(details: PendantDetailsEntity)

    @Query("SELECT * FROM pendant_details WHERE itemId = :itemId")
    suspend fun getPendantDetails(itemId: String): PendantDetailsEntity?

    // ── Bracelet ────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBraceletDetails(details: BraceletDetailsEntity)

    @Query("SELECT * FROM bracelet_details WHERE itemId = :itemId")
    suspend fun getBraceletDetails(itemId: String): BraceletDetailsEntity?

    // ── Earrings ────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarringsDetails(details: EarringsDetailsEntity)

    @Query("SELECT * FROM earrings_details WHERE itemId = :itemId")
    suspend fun getEarringsDetails(itemId: String): EarringsDetailsEntity?

    // ── Brooch ──────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBroochDetails(details: BroochDetailsEntity)

    @Query("SELECT * FROM brooch_details WHERE itemId = :itemId")
    suspend fun getBroochDetails(itemId: String): BroochDetailsEntity?

    // ── Pearl ───────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPearlDetails(details: PearlDetailsEntity)

    @Query("SELECT * FROM pearl_details WHERE itemId = :itemId")
    suspend fun getPearlDetails(itemId: String): PearlDetailsEntity?

}