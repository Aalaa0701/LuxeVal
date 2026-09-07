package com.example.luxeval.data.database.daos

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AppDao {
    @Query("DELETE FROM items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM clients")
    suspend fun deleteAllClients()

    @Query("DELETE FROM appraisals")
    suspend fun deleteAllAppraisals()
}