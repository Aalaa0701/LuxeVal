package com.example.luxeval.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.luxeval.data.database.entities.PendingPhotoUploadEntity

@Dao
interface PendingPhotoUploadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: PendingPhotoUploadEntity)

    @Query("SELECT * FROM pending_photo_uploads ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingPhotoUploadEntity>

    @Query("DELETE FROM pending_photo_uploads WHERE id = :id")
    suspend fun remove(id: String)

    @Query("DELETE FROM pending_photo_uploads WHERE itemId = :itemId")
    suspend fun removeAllForItem(itemId: String)

    @Query("UPDATE pending_photo_uploads SET attempts = attempts + 1, lastError = :error WHERE id = :id")
    suspend fun recordFailure(id: String, error: String?)
}